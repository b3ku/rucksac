package org.rucksac.parser

import org.rucksac.{AttributeOperationNotSupportedException, PseudoFunctionNotSupportedException, PseudoClassNotSupportedException, NodeBrowser}

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait Condition extends Matchable

final class NegativeCondition(con: Condition) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = !con.matches(node, browser)

    override def toString = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = sel.matches(node, browser)

    override def toString = sel.toString

}

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = first.matches(node, browser) && second.matches(node, browser)

    override def toString = first.toString + second.toString

}

final class Attribute(uri: String, localName: String, value: String, op: String)
    extends Qualifiable(uri, localName) with Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = {
        val attrValue = browser.attribute(node, uri, localName)
        op match {
            case "#" => attrValue == value
            case "." => Option(attrValue).orElse(Option("")).get.split(" ") contains value
            case "=" => attrValue == value
            case "~=" => Option(attrValue).orElse(Option("")).get.split(" ") contains value
            case "|=" => attrValue == value ||
                Option(attrValue).orElse(Option("")).get.startsWith(value + "-")
            case "^=" => Option(attrValue).orElse(Option("")).get startsWith value
            case "$=" => Option(attrValue).orElse(Option("")).get endsWith value
            case "*=" => Option(attrValue).orElse(Option("")).get contains value
            case null => attrValue != null && attrValue != ""
            case _ => throw new AttributeOperationNotSupportedException(op)
        }
    }

    override def toString = op match {
        case "#" => "#" + value
        case "." => "." + value
        case _ => "[" + super.toString + (if (value == null) "" else op + value) + "]"
    }

}

final class PseudoClass(name: String) extends Condition {

    import scala.collection.JavaConversions._

    def matches[T](node: T, browser: NodeBrowser[T]) = {
        def ofType(f: Iterable[T] => Boolean): Boolean = {
            val children: Iterable[T] = Option(browser.parent(node)).map({browser.children(_)}).get
            val (name, namespaceUri) = (browser.name(node), browser.namespaceUri(node))
            children != null && f(children
                .filter({c => browser.name(c) == name && browser.namespaceUri(c) == namespaceUri}))
        }
        name match {
            case "first-child" => Option(browser.parent(node)).map({browser.children(_).indexOf(node)}).get == 0
            case "last-child" => {
                val parent = Option(browser.parent(node))
                parent.map({browser.children(_).indexOf(node)}).get ==
                    parent.map({browser.children(_).size() - 1}).getOrElse(0)
            }
            case "only-child" => Option(browser.parent(node)).map({browser.children(_).size}).get == 1
            case "empty" => Option(browser.children(node)).map({_.size()}).get == 0
            case "root" => browser.document(node) == browser.parent(node)
            case "only-of-type" => ofType {_.size == 1}
            case "first-of-type" => ofType {_.head == node}
            case "last-of-type" => ofType {_.last == node}
            case _ => throw new PseudoClassNotSupportedException(name)
        }
    }

    override def toString = ":" + name

}

final class PseudoFunction(name: String, exp: String) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = name match {
        case _ => throw new PseudoFunctionNotSupportedException(name)
    }

    override def toString = ":" + name + "(" + exp + ")"
}
