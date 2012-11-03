package org.rucksac.parser

import org.rucksac._
import matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait Condition extends (Node => Boolean)

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def apply(node: Node) = first(node) && second(node)

    override def toString() = first.toString + second.toString

}

final class NegativeCondition(con: Condition) extends Condition {

    def apply(node: Node) = !con(node)

    override def toString() = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition {

    def apply(node: Node) = sel(node)

    override def toString() = sel.toString()

}

final class AttributeCondition(uri: String, name: String, value: String, op: String)
    extends Condition {

    def apply(node: Node) = node.isElement && {
        val attrValue = node.attribute(uri, name)
        op match {
            case "#" | "=" => attrValue == value
            case "." | "~=" => attrValue != null && attrValue.split(" ").contains(value)
            case "|=" => attrValue != null && (attrValue == value || attrValue.startsWith(value + "-"))
            case "^=" => attrValue != null && attrValue.startsWith(value)
            case "$=" => attrValue != null && attrValue.endsWith(value)
            case "*=" => attrValue != null && attrValue.contains(value)
            case null => attrValue != null
            case s: String => NodeMatcherRegistry().attributeOperations(s)(attrValue, value)
        }
    }

    override def toString() = op match {
        case "#" | "." => op + value
        case _ => "[" + QualifiedName(uri, name) + (if (value == null) "" else op + value) + "]"
    }

}

final class PseudoClassCondition(pc: String) extends Condition {

    def apply(node: Node) = pc match {
        case "first-child" => node.siblings.indexOf(node) == 0
        case "last-child" =>
            val children = node.siblings
            children.indexOf(node) == children.size - 1
        case "only-child" => node.siblings.size == 1
        case "only-of-type" => node.siblingsOfSameType.size == 1
        case "first-of-type" => node.siblingsOfSameType.head == node
        case "last-of-type" => node.siblingsOfSameType.last == node
        case "root" => node.parent == None
        case "empty" => node.children.isEmpty
        case "enabled" => node.isElement && node.attribute("disabled") != "disabled"
        case "disabled" => node.isElement && node.attribute("disabled") == "disabled"
        case "checked" => node.isElement && node.attribute("checked") == "checked"
        case s: String => NodeMatcherRegistry().pseudoClasses(s)(node)
    }

    override def toString() = ":" + pc

}

final class PseudoFunctionCondition(name: String, exp: String) extends Condition {

    lazy val positionMatcher = NthParser.parse(exp)

    def apply(node: Node) = name match {
        case "nth-child" => positionMatcher.matches(node.siblings.indexOf(node) + 1)
        case "nth-last-child" =>
            val children = node.siblings
            positionMatcher.matches(children.size - children.indexOf(node))
        case "nth-of-type" => positionMatcher.matches(node.siblingsOfSameType.indexOf(node) + 1)
        case "nth-last-of-type" =>
            val siblings = node.siblingsOfSameType
            positionMatcher.matches(siblings.size - siblings.indexOf(node))
        case "contains" => node.textNodes.filter(_.contains(exp)).nonEmpty
        case "lang" =>
            val matches: Node => Boolean = {
                p: Node =>
                    val lang: String = Option(p.attribute("lang")).getOrElse("")
                    lang == exp || lang.startsWith(exp + "-")
            }
            (node.isElement && matches(node)) || node.matchesAnyParent(matches)
        case s: String => NodeMatcherRegistry().pseudoFunctions(s)(node, exp)
    }

    override def toString() = ":" + name + "(" + exp + ")"

}
