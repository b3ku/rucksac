package org.rucksac.parser

import org.rucksac.NodeBrowser
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

final class AttributeCondition(uri: String, name: String, value: String, op: String)
    extends Qualifiable(uri, name) with Condition {

    def apply[T](node: T, browser: NodeBrowser[T]) = browser.isElement(node) && {
        lazy val attrValue = attribute(node, browser, uri, name)
        op match {
            case "#" | "=" => attrValue == value
            case "." | "~=" => attrValue.split(" ") contains value
            case "|=" => attrValue == value || attrValue.startsWith(value + "-")
            case "^=" => attrValue startsWith value
            case "$=" => attrValue endsWith value
            case "*=" => attrValue contains value
            case null => browser.attribute(node, uri, name) != null
            case s: String => browser.findAttributeOperationMatcher(s)(node, browser, uri, name, value)
        }
    }

    override def toString = op match {
        case "#" | "." => op + value
        case _ => "[" + super.toString + (if (value == null) "" else op + value) + "]"
    }

}

final class PseudoClassCondition(pc: String) extends Condition {

    def apply[T](node: T, browser: NodeBrowser[T]): Boolean = pc match {
        case "first-child" => siblings(node, browser).indexOf(node) == 0
        case "last-child" =>
            val children = siblings(node, browser)
            children.indexOf(node) == children.size - 1
        case "only-child" => siblings(node, browser).size == 1
        case "only-of-type" => siblingsOfSameType(node, browser).size == 1
        case "first-of-type" => siblingsOfSameType(node, browser).head == node
        case "last-of-type" => siblingsOfSameType(node, browser).last == node
        case "root" => browser.parent(node) == null
        case "empty" => children(node, browser).isEmpty
        case "enabled" => browser.isElement(node) && attribute(node, browser, "disabled") != "disabled"
        case "disabled" => browser.isElement(node) && attribute(node, browser, "disabled") == "disabled"
        case "checked" => browser.isElement(node) && attribute(node, browser, "checked") == "checked"
        case s: String => browser.findPseudoClassMatcher(s)(node, browser)
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
        case "nth-of-type" => positionMatcher.matches(siblingsOfSameType(node, browser).indexOf(node) + 1)
        case "nth-last-of-type" =>
            val siblings = siblingsOfSameType(node, browser)
            positionMatcher.matches(siblings.size - siblings.indexOf(node))
        case "contains" => textNodes(children(node, browser), browser).filter(_.contains(exp)).nonEmpty
        case "lang" =>
            val matches: (T) => Boolean = {
                p: T =>
                    val lang: String = attribute(p, browser, "lang")
                    lang == exp || lang.startsWith(exp + "-")
            }
            (browser.isElement(node) && matches(node)) || matchesAnyParent(node, browser, matches)
        case s: String => browser.findPseudoFunctionMatcher(s)(node, browser, exp)
    }

    override def toString = ":" + name + "(" + exp + ")"

}
