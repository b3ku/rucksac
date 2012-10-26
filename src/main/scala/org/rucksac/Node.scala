package org.rucksac

import collection._

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */

case class Node[T](node: T, p: Option[Node[T]], browser: NodeBrowser[T]) {

    def apply(): T = node

    def parent = p match {
        case Some(n) if (!n.isDocument) => Option(n)
        case _ => None
    }

    def children: Seq[Node[T]] = {
        val children = browser.children(node)
        val parent = Option(this)
        new immutable.IndexedSeq[Node[T]] {
            def length = children.size

            def apply(idx: Int) = {
                Node(children(idx), parent, browser)
            }
        }
    }

    def textNodes = children filter { _.isText } map { _.text }

    def attribute(uri: String, name: String) = browser.attribute(node, uri, name)

    def attribute(name: String): String = attribute(null, name)

    def text = Option(browser.text(node)).getOrElse("")

    def name = browser.name(node)

    def namespaceUri = Option(browser.namespaceUri(node)).getOrElse("")

    def isDocument = browser.isDocument(node)

    def isElement = browser.isElement(node)

    def isText = browser.isText(node)

    def siblings: Seq[Node[T]] = parent.map(_.children).getOrElse(List(this)).filter(_.isElement)

    def matchesAnyParent(m: Node[T] => Boolean): Boolean = parent.map({ p => m(p) || p.matchesAnyParent(m) })
            .getOrElse(false)

    def siblingsOfSameType = {
        val expName = (name, namespaceUri)
        siblings filter { n => n.isElement && (n.name, n.namespaceUri) == expName }
    }

    override def toString = name

}
