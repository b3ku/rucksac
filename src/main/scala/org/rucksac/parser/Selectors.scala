package org.rucksac.parser

import org.rucksac.{utils,NodeBrowser, NodeMatcher}
import collection.mutable

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

trait Selector extends NodeMatcher

trait SimpleSelector extends Selector

final class ConditionalSelector(sel: SimpleSelector, con: Condition) extends Selector {

    def matches[T](node: T, browser: NodeBrowser[T]) = sel.matches(node, browser) && con.matches(node, browser)

    override def toString = sel.toString + con.toString

}

class ElementSelector(uri: String, name: String) extends Qualifiable(uri, name) with SimpleSelector {

    def matches[T](node: T, browser: NodeBrowser[T]) = {
        var matches = browser.isElement(node)
        if (matches) {
            matches = name == null
            if (!matches) {
                matches = name == browser.name(node) && (uri == null || uri == browser.namespaceUri(node))
            }
        }
        matches
    }

}

object Any extends ElementSelector(null, null)

final class SelectorCombinator(left: Selector, combinator: CombinatorType, right: Selector) extends Selector {

    import scala.collection.JavaConversions._

    def matches[T](node: T, browser: NodeBrowser[T]) = right.matches(node, browser) && (combinator.op match {
        case ">" => Option(browser.parent(node)).map({left.matches(_, browser)}).getOrElse(false)
        case " " => utils.matchesAnyParent(node, browser, {p:T => left.matches(p, browser)})
        case "+" =>
            val siblings = utils.siblingsAndMe(node, browser)
            val index = siblings.indexOf(node)
            index > 0 && left.matches(siblings.get(index - 1), browser)
        case "~" =>
            val children: mutable.Buffer[_ <: T] = utils.siblingsAndMe(node, browser)
            children.take(children.indexOf(node)).filter({left.matches(_, browser)}).nonEmpty
    })

    override def toString = left.toString + combinator + right

}


