package org

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */

package object rucksac {

    class Node[T](node: T) {

        private val browser = NodeBrowser[T]()

        def apply(): T = node

        def parent(): Option[Node[T]] = Option(browser.parent(node))

        def children() = browser.children(node)

        def textNodes() = children() filter {_.isText()} map {_.text()}

        def attribute(uri: String, name: String) = browser.attribute(node, uri, name)

        def attribute(name: String): String = attribute(null, name)

        def text() = Option(browser.text(node)).getOrElse("")

        def name() = browser.name(node)

        def namespaceUri() = Option(browser.namespaceUri(node)).getOrElse("")

        def isElement() = browser.isElement(node)

        def isText() = browser.isText(node)

        def siblings(): Seq[T] = parent().map({_.children()}).getOrElse(List(node)).filter({_.isElement()})

        def matchesAnyParent(m: T => Boolean): Boolean = parent().map({p => m(p()) || p.matchesAnyParent(m)})
            .getOrElse(false)

        def siblingsOfSameType() = {
            val expName = (name(), namespaceUri())
            siblings().filter(n => n.isElement() && (n.name(), n.namespaceUri()) == expName)
        }

    }

    implicit def asNode[T](n: T): Node[T] = if (n.isInstanceOf[Node[T]]) {
        n.asInstanceOf[Node[T]]
    } else {
        Option(n).map({new Node[T](_)}).getOrElse(null)
    }

}
