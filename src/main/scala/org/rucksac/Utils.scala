package org.rucksac

import collection.mutable
import java.util.Collections

/**
 * @author Andreas Kuhrwahl
 * @since 19.08.12
 */

package object utils {

    import scala.collection.JavaConversions._

    def parent[T](node: T, browser: NodeBrowser[T]) = Option(browser.parent(node))

    def children[T](node: T, browser: NodeBrowser[T]): mutable.Buffer[_ <: T] =
        Option(browser.children(node)).getOrElse(Collections.emptyList())

    def attribute[T](node: T, browser: NodeBrowser[T], uri: String, name: String) =
        Option(browser.attribute(node, uri, name)).getOrElse("")

    def text[T](node: T, browser: NodeBrowser[T]) = Option(browser.text(node)).getOrElse("")

    def namespaceUri[T](node: T, browser: NodeBrowser[T]) = Option(browser.namespaceUri(node)).getOrElse("")

    def textNodes[T](nodes: Iterable[T], browser: NodeBrowser[T]): Iterable[String] =
        nodes.filter({browser.isText(_)}).map({browser.text(_)})

    def matchesAnyParent[T](node: T, browser: NodeBrowser[T], matches: T => Boolean): Boolean = {
        val parent = browser.parent(node)
        parent != null && (matches(parent) || matchesAnyParent(parent, browser, matches))
    }

    def siblings[T](node: T, browser: NodeBrowser[T]) = parent(node, browser) map {children(_, browser)} getOrElse
        (List(node))

}
