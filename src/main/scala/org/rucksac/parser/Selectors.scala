package org.rucksac.parser

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

    def matches[T](node: T, browser: NodeBrowser[T]) = sel.matches(node, browser) & con.matches(node, browser)

    override def toString = sel.toString + con.toString

}

class ElementSelector(prefix: String, name: String) extends Qualifiable(prefix, name) with SimpleSelector {

    def matches[T](node: T, browser: NodeBrowser[T]) = true

}

object Any extends ElementSelector(null, null)

final class SelectorCombinator(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    def matches[T](node: T, browser: NodeBrowser[T]) = true

    override def toString = left.toString + combinator + right

}


