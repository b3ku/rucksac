package org.rucksac.parser

import org.rucksac._
import matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait Condition extends Matchable

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def apply[T](node: Node[T]) = first(node) && second(node)

    override def toString = first.toString + second.toString

}

final class NegativeCondition(con: Condition) extends Condition {

    def apply[T](node: Node[T]) = !con(node)

    override def toString = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition {

    def apply[T](node: Node[T]) = sel(node)

    override def toString = sel.toString

}

final class AttributeCondition(uri: String, name: String, value: String, op: String) extends Condition {

    def apply[T](node: Node[T]) = node.isElement && {
        lazy val attrValue = Option(node.attribute(uri, name)).getOrElse("")
        op match {
            case "#" | "=" => attrValue == value
            case "." | "~=" => attrValue.split(" ") contains value
            case "|=" => attrValue == value || attrValue.startsWith(value + "-")
            case "^=" => attrValue startsWith value
            case "$=" => attrValue endsWith value
            case "*=" => attrValue contains value
            case null => node.attribute(uri, name) != null
            case s: String => NodeMatcherRegistry().attributeOperations(s)(node, uri, name, value)
        }
    }

    override def toString = op match {
        case "#" | "." => op + value
        case _ => "[" + QualifiedName(uri, name) + (if (value == null) "" else op + value) + "]"
    }

}

final class PseudoClassCondition(pc: String) extends Condition {

    def apply[T](node: Node[T]) = pc match {
        case "first-child" =>
            val of: Int = node.siblings.indexOf(node)
            println(of)
            of == 0
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
        //case s: String => NodeMatcherRegistry().pseudoClasses(s)(node, nodes)
        case _ => throw new ParseException("not supported")
    }

    override def toString = ":" + pc

}

final class PseudoFunctionCondition(name: String, exp: String) extends Condition {

    lazy val positionMatcher = NthParser.parse(exp)

    def apply[T](node: Node[T]) = name match {
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
            val matches: Node[T] => Boolean = {
                p: Node[T] =>
                    val lang: String = Option(p.attribute("lang")).getOrElse("")
                    lang == exp || lang.startsWith(exp + "-")
            }
            (node.isElement && matches(node)) || node.matchesAnyParent(matches)
        //case s: String => NodeMatcherRegistry().pseudoFunctions(s)(node, nodes, exp)
        case _ => throw new ParseException("not supported")
    }

    override def toString() = ":" + name + "(" + exp + ")"

}
