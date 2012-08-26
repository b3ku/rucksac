package org.rucksac.parser

import org.rucksac.utils._
import org.rucksac.NodeBrowser

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

    def apply[T](nodes: List[T], browser: NodeBrowser[T]) = con(sel(nodes, browser), browser)

    override def toString = sel.toString + con.toString

}

class ElementSelector(uri: String, tagName: String) extends Qualifiable(uri, tagName) with SimpleSelector {

    def apply[T](nodes: List[T], browser: NodeBrowser[T]) = nodes filter {
        node => browser.isElement(node) &&
            (tagName == null || tagName == browser.name(node)) &&
            (uri == null || uri == namespaceUri(node, browser))
    }

}

object Any extends ElementSelector(null, null)

final class SelectorCombinatorSelector(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    def apply[T](nodes: List[T], browser: NodeBrowser[T]) = right(nodes, browser) filter { node =>
        (combinator.op match {
            case ">" => parent(node, browser) map {p => left(List(p), browser).nonEmpty} getOrElse false
            case " " => matchesAnyParent(node, browser, {p: T => left(List(p), browser).nonEmpty})
            case "+" =>
                val children = siblings(node, browser)
                val index = children.indexOf(node)
                index > 0 && left(children.slice(index - 1, index), browser).nonEmpty
            case "~" =>
                val children = siblings(node, browser)
                left(children take children.indexOf(node), browser).nonEmpty
            case s: String => browser.findSelectorCombinatorMatcher(s)(node, browser)
        })
    }

    override def toString = left.toString + combinator + right

}


