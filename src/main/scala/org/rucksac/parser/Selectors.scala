package org.rucksac.parser

import org.rucksac.NodeBrowser
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

trait Selector extends Matchable

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
        case " " => {
            def matches(parent: T): Boolean = {
                var result = false
                if (parent != null) {
                    result = left.matches(parent, browser)
                    if (!result) {
                        result = matches(browser.parent(parent))
                    }
                }
                return result
            }
            matches(browser.parent(node))
        }
        case "+" => {
            val parent = browser.parent(node);
            var result = false
            if (parent != null) {
                val children = browser.children(parent)
                val index = children.indexOf(node)
                if (index > 0) {
                    result = left.matches(children.get(index - 1), browser)
                }
            }
            result
        }
        case "~" => {
            val parent = browser.parent(node)
            var result = false
            if (parent != null) {
                val children: mutable.Buffer[_ <: T] = browser.children(parent)
                result = children.take(children.indexOf(node)).filter({left.matches(_, browser)}).nonEmpty
            }
            result
        }
    })

    override def toString = left.toString + combinator + right

}


