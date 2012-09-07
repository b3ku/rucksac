package org.rucksac.parser

import org.rucksac._
import matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

case class CombinatorType(op: String) {

    override def toString = op match {
        case " " => " "
        case _ => " " + op + " "
    }

}

trait Selector extends Matchable

trait SimpleSelector extends Selector

final class ConditionalSelector(sel: SimpleSelector, con: Condition) extends Selector {

    def apply[T](node: Node[T]) = sel(node) && con(node)

    def apply[T](nodes: List[Node[T]]) = con(sel(nodes))

    override def toString = sel.toString + con.toString

}

class ElementSelector(uri: String, tagName: String) extends SimpleSelector with ListMatchable {

    def apply[T](node: Node[T]) = node.isElement && (tagName == null || tagName == node.name) &&
            (uri == null || uri == node.namespaceUri)

    override def toString = QualifiedName(uri, tagName).toString

}

object ElementSelector {
    def any() = new ElementSelector(null, null)
}

class SelectorCombinatorSelector(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    private def matchLeft[T](node: Node[T]) = (combinator.op match {
        case ">" => node.parent map { left(_)} getOrElse false
        case " " => node.matchesAnyParent({ left(_) })
        case "+" =>
            val children = node.siblings
            val index = children.indexOf(node)
            index > 0 && left(children(index - 1))
        case "~" =>
            val children = node.siblings
            children.take(children.indexOf(node)).filter({ left(_) }).nonEmpty
        case s: String => NodeMatcherRegistry().selectorCombinators(s)(node)
    })

    def apply[T](node: Node[T]) = right(node) && matchLeft(node)

    def apply[T](nodes: List[Node[T]]) = right(nodes) filter { matchLeft(_) }

    override def toString = left.toString + combinator.toString + right

}


