package org.rucksac.parser

import org.rucksac.{NodeBrowser, PseudoClassNotSupportedException, PseudoFunctionNotSupportedException, AttributeOperationNotSupportedException}
import org.rucksac.utils._

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait Condition extends Matchable

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def apply[T](node: T, browser: NodeBrowser[T]) = first(node, browser) && second(node, browser)

    override def toString = first.toString + second.toString

}

final class NegativeCondition(con: Condition) extends Condition {

    def apply[T](node: T, browser: NodeBrowser[T]) = !con(node, browser)

    override def toString = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition {

    def apply[T](node: T, browser: NodeBrowser[T]) = sel(node, browser)

    override def toString = sel.toString

}

final class AttributeCondition(uri: String, localName: String, value: String, operation: String)
    extends Qualifiable(uri, localName) with Condition {

    def apply[T](node: T, browser: NodeBrowser[T]) = {
        val attrValue = attribute(node, browser, uri, localName)
        operation match {
            case "#" | "=" => attrValue == value
            case "." | "~=" => attrValue.split(" ") contains value
            case "|=" => attrValue == value || attrValue.startsWith(value + "-")
            case "^=" => attrValue startsWith value
            case "$=" => attrValue endsWith value
            case "*=" => attrValue contains value
            case null => attrValue != ""
            case _ => throw new AttributeOperationNotSupportedException(operation)
        }
    }

    override def toString = operation match {
        case "#" | "." => operation + value
        case _ => "[" + super.toString + (if (value == null) "" else operation + value) + "]"
    }

}

final class PseudoClassCondition(pc: String) extends Condition {

    def apply[T](node: T, browser: NodeBrowser[T]): Boolean = {
        def ofType(f: Iterable[T] => Boolean): Boolean = {
            val (nodeName, nodeNamespaceUri) = (browser.name(node), namespaceUri(node, browser))
            f(siblings(node, browser).filter({
                c => browser.isElement(c) && browser.name(c) == nodeName && namespaceUri(c, browser) == nodeNamespaceUri
            }))
        }
        pc match {
            case "first-child" => siblings(node, browser).indexOf(node) == 0
            case "last-child" =>
                val children = siblings(node, browser)
                children.indexOf(node) == children.size - 1
            case "only-child" => siblings(node, browser).size == 1
            case "only-of-type" => ofType {_.size == 1}
            case "first-of-type" => ofType {_.head == node}
            case "last-of-type" => ofType {_.last == node}
            case "root" => document(node, browser).get == parent(node, browser).get
            case "empty" => children(node, browser).isEmpty
            case "enabled" => attribute(node, browser, null, "disabled") != "disabled"
            case "disabled" => attribute(node, browser, null, "disabled") == "disabled"
            case "checked" => attribute(node, browser, null, "checked") == "checked"
            case _ => throw new PseudoClassNotSupportedException(pc)
        }
    }

    override def toString = ":" + pc

}

final class PseudoFunctionCondition(name: String, exp: String) extends Condition {

    lazy val positionMatcher = NthParser.parse(exp)

    def apply[T](node: T, browser: NodeBrowser[T]) = name match {
        case "nth-child" => positionMatcher.matches(siblings(node, browser).indexOf(node) + 1)
        case "nth-last-child" =>
            val children = siblings(node, browser)
            positionMatcher.matches(children.size - children.indexOf(node))
        case "contains" => textNodes(children(node, browser), browser).filter(_.contains(exp)).nonEmpty
        case "lang" =>
            val matches: (T) => Boolean = {
                p: T =>
                    val lang: String = attribute(p, browser, null, "lang")
                    lang == exp || lang.startsWith(exp + "-")
            }
            matches(node) || matchesAnyParent(node, browser, matches)
        case _ => throw new PseudoFunctionNotSupportedException(name)
    }

    override def toString = ":" + name + "(" + exp + ")"
}
