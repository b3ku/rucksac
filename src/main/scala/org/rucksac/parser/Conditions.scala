package org.rucksac.parser

import org.rucksac.{utils, PseudoFunctionNotSupportedException, PseudoClassNotSupportedException, AttributeOperationNotSupportedException, NodeBrowser, NodeMatcher}

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait Condition extends NodeMatcher

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = first.matches(node, browser) && second.matches(node, browser)

    override def toString = first.toString + second.toString

}

final class NegativeCondition(con: Condition) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = !con.matches(node, browser)

    override def toString = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = sel.matches(node, browser)

    override def toString = sel.toString

}

final class Attribute(uri: String, localName: String, value: String, op: String)
    extends Qualifiable(uri, localName) with Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = {
        val attrValue = Option(browser.attribute(node, uri, localName)).orElse(Option("")).get
        op match {
            case "#" | "=" => attrValue == value
            case "." | "~=" => attrValue.split(" ") contains value
            case "|=" => attrValue == value || attrValue.startsWith(value + "-")
            case "^=" => attrValue startsWith value
            case "$=" => attrValue endsWith value
            case "*=" => attrValue contains value
            case null => attrValue != ""
            case _ => throw new AttributeOperationNotSupportedException(op)
        }
    }

    override def toString = op match {
        case "#" | "." => op + value
        case _ => "[" + super.toString + (if (value == null) "" else op + value) + "]"
    }

}

final class PseudoClass(name: String) extends Condition {

    import scala.collection.JavaConversions._

    def matches[T](node: T, browser: NodeBrowser[T]) = {
        def ofType(f: Iterable[T] => Boolean): Boolean = {
            val (name, namespaceUri) = (browser.name(node), browser.namespaceUri(node))
            val isType: T => Boolean = {
                c => browser.isElement(c) && browser.name(c) == name && browser.namespaceUri(c) == namespaceUri
            }
            val siblings: Iterable[T] = utils.siblingsAndMe(node, browser)
            f(siblings.filter(isType))
        }
        name match {
            case "first-child" => utils.siblingsAndMe(node, browser).indexOf(node) == 0
            case "last-child" =>
                val siblings = utils.siblingsAndMe(node, browser)
                siblings.indexOf(node) == siblings.size - 1
            case "only-child" => utils.siblingsAndMe(node, browser).size == 1
            case "only-of-type" => ofType {_.size == 1}
            case "first-of-type" => ofType {_.head == node}
            case "last-of-type" => ofType {_.last == node}
            case "root" => browser.document(node) == browser.parent(node)
            case "empty" => Option(browser.children(node)).map({_.size()}).get == 0
            case "enabled" => Option(browser.attribute(node, null, "disabled")).getOrElse("enabled") != "disabled"
            case "disabled" => browser.attribute(node, null, "disabled") == "disabled"
            case "checked" => browser.attribute(node, null, "checked") == "checked"
            case _ => throw new PseudoClassNotSupportedException(name)
        }
    }

    override def toString = ":" + name

}

final class PseudoFunction(name: String, exp: String) extends Condition {

    import scala.collection.JavaConversions._

    lazy val positionMatcher = NthParser.parse(exp)

    def matches[T](node: T, browser: NodeBrowser[T]) = name match {
        case "nth-child" =>
            val siblings = utils.siblingsAndMe(node, browser)
            positionMatcher.matches(siblings.indexOf(node) + 1)
        case "nth-last-child" =>
            val siblings = utils.siblingsAndMe(node, browser)
            positionMatcher.matches(siblings.size - siblings.indexOf(node))
        case "contains" => {
            val children: Iterable[T] = browser.children(node)
            children != null && children.filter(browser.isText(_)).map(browser.text(_)).filter(_.contains(exp)).nonEmpty
        }
        case "lang" =>
            val matches: (T) => Boolean = {
                p: T =>
                    val lang: String = Option(browser.attribute(p, null, "lang")).getOrElse("")
                    lang == exp || lang.startsWith(exp + "-")
            }
            matches(node) || utils.matchesAnyParent(node, browser, matches)
        case _ => throw new PseudoFunctionNotSupportedException(name)
    }

    override def toString = ":" + name + "(" + exp + ")"
}
