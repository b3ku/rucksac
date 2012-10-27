package org.rucksac.parser

import org.rucksac._
import matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @author Oliver Becker
 * @since 15.08.12
 */

trait Condition extends Matchable

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def apply[T](node: Node[T]) = first(node) && second(node)

    def apply[T](nodes: Seq[Node[T]]) = second(first(nodes))

    lazy val mustFilter = first.mustFilter || second.mustFilter

    override def toString = first.toString + second.toString

}

final class NegativeCondition(con: Condition) extends Condition with SeqMatchable {

    def apply[T](node: Node[T]) = !con(node)

    lazy val mustFilter = con.mustFilter

    override def toString = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition with SeqMatchable {

    def apply[T](node: Node[T]) = sel(node)

    lazy val mustFilter = sel.mustFilter

    override def toString = sel.toString

}

final class AttributeCondition(uri: String, name: String, value: String, op: String) extends Condition with SeqMatchable {

    def apply[T](node: Node[T]) = node.isElement && {
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

    val mustFilter = false

    override def toString = op match {
        case "#" | "." => op + value
        case _ => "[" + QualifiedName(uri, name) + (if (value == null) "" else op + value) + "]"
    }

}

final class PseudoClassCondition(pc: String) extends Condition {

    private def matches[T](node: Node[T], nodes: Seq[Node[T]] = List.empty) = pc match {
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
        case s: String =>
            if (mustFilter) NodeMatcherRegistry().positionalPseudoClasses(s)(node, nodes)
            else NodeMatcherRegistry().simplePseudoClasses(s)(node)
    }

    def apply[T](node: Node[T]) = matches(node)

    def apply[T](nodes: Seq[Node[T]]) = nodes filter { matches(_, nodes) }

    val mustFilter = NodeMatcherRegistry().positionalPseudoClasses.contains(pc)

    override def toString = ":" + pc

}

final class PseudoFunctionCondition(name: String, exp: String) extends Condition {

    lazy val positionMatcher = NthParser.parse(exp)

    private def matches[T](node: Node[T], nodes: Seq[Node[T]] = List.empty) = name match {
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
        case s: String =>
            if (mustFilter) NodeMatcherRegistry().positionalPseudoFunctions(s)(node, nodes, exp)
            else NodeMatcherRegistry().simplePseudoFunctions(s)(node, exp)
    }

    def apply[T](node: Node[T]) = matches(node)

    def apply[T](nodes: Seq[Node[T]]) = nodes filter { matches(_, nodes) }

    val mustFilter = NodeMatcherRegistry().positionalPseudoFunctions.contains(name)

    override def toString = ":" + name + "(" + exp + ")"

}
