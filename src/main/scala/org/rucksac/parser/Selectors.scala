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

    def apply[T](node: T, browser: NodeBrowser[T]) = sel(node, browser) && con(node, browser)

    override def toString = sel.toString + con.toString

}

class ElementSelector(uri: String, tagName: String) extends Qualifiable(uri, tagName) with SimpleSelector {

    def apply[T](node: T, browser: NodeBrowser[T]) = browser.isElement(node) &&
        (tagName == null || (tagName == browser.name(node) && (uri == null || uri == namespaceUri(node, browser))))

}

object Any extends ElementSelector(null, null)

final class SelectorCombinatorSelector(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    def apply[T](node: T, browser: NodeBrowser[T]) = right(node, browser) && (combinator.op match {
        case ">" => parent(node, browser).map({left(_, browser)}).getOrElse(false)
        case " " => matchesAnyParent(node, browser, {p: T => left(p, browser)})
        case "+" =>
            val children = siblings(node, browser)
            val index = children.indexOf(node)
            index > 0 && left(children(index - 1), browser)
        case "~" =>
            val children = siblings(node, browser)
            children.take(children.indexOf(node)).filter({left(_, browser)}).nonEmpty
        case s: String => browser.findSelectorCombinatorMatcher(s)(node, browser)
    })

    override def toString = left.toString + combinator + right

}


