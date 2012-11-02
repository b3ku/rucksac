package org.rucksac.parser

import css.Query
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

final class Selectors(selectors: List[Selector]) extends Selector {

    def apply[T](node: Node[T]): Boolean = (false /: selectors)(_ || _(node))

    def apply[T](nodes: Seq[Node[T]]): Seq[Node[T]] =
        if (mustFilter) nodes intersect (List[Node[T]]() /: selectors)(_ ++ _(nodes))
        else nodes.filter(apply(_))

    def filter[T](nodes: Seq[Node[T]]): Seq[Node[T]] =
        if (mustFilter && usesCombinators) nodes intersect new Query(nodes.map(_.root).distinct).@@(this)
        else apply(nodes)

    lazy val mustFilter = (false /: selectors)(_ || _.mustFilter)

    override lazy val usesCombinators = (false /: selectors)(_ || _.usesCombinators)

    override def toString = selectors mkString ", "

}

final class ConditionalSelector(sel: SimpleSelector, con: Condition) extends Selector {

    def apply[T](node: Node[T]) = sel(node) && con(node)

    def apply[T](nodes: Seq[Node[T]]) = con(sel(nodes))

    lazy val mustFilter = con.mustFilter

    override def toString = sel.toString + con.toString

}

class ElementSelector(uri: String, tagName: String) extends SimpleSelector with SeqMatchable {

    def apply[T](node: Node[T]) = node.isElement && (tagName == null || tagName == node.name) &&
            (uri == null || uri == node.namespaceUri)

    val mustFilter = false

    override def toString = QualifiedName(uri, tagName).toString

}

object ElementSelector {
    def any() = new ElementSelector(null, null)
}

class SelectorCombinatorSelector(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    def apply[T](node: Node[T]) = right(node) && (combinator.op match {
        case ">" => node.parent map { left(_) } getOrElse false
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

    def apply[T](nodes: Seq[Node[T]]): Seq[Node[T]] = {
        val leftNodes = new Query(left(nodes))
        combinator.op match {
            case ">" => leftNodes.@@>(right)
            case " " => leftNodes.@@(right)
            case "+" => leftNodes.@@+(right)
            case "~" => leftNodes.@@~(right)
            case s: String => NodeMatcherRegistry().selectorCombinators(s)(nodes)
        }
    }

    lazy val mustFilter = left.mustFilter || right.mustFilter

    override val usesCombinators = true

    override def toString = left.toString + combinator.toString + right

}


