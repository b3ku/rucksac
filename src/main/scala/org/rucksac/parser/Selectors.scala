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

    def apply[T](nodes: Seq[T]) = con(sel(nodes))

    override def toString = sel.toString + con.toString

}

class ElementSelector(uri: String, tagName: String) extends Qualifiable(uri, tagName) with SimpleSelector {

    def apply[T](nodes: Seq[T]) = nodes filter {
        node => node.isElement() && (tagName == null || tagName == node.name()) &&
            (uri == null || uri == node.namespaceUri())
    }

}

object Any extends ElementSelector(null, null)

final class SelectorCombinatorSelector(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    def apply[T](nodes: Seq[T]) = right(nodes) filter {
        node =>
            (combinator.op match {
                case ">" => node.parent() map {p => left(List(p)).nonEmpty} getOrElse false
                case " " => node.matchesAnyParent({p => left(List(p)).nonEmpty})
                case "+" =>
                    val children = node.siblings()
                    val index = children.indexOf(node)
                    index > 0 && left(children.slice(index - 1, index)).nonEmpty
                case "~" =>
                    val children = node.siblings()
                    left(children take children.indexOf(node)).nonEmpty
                case s: String => NodeMatcherRegistry().selectorCombinators(s)(node)
            })
    }

    override def toString = left.toString + combinator + right

}


