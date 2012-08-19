package org.rucksac.parser.css

import org.rucksac.NodeBrowser
import org.w3c.dom.{Element, Node}

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */
class DomNodeBrowser extends NodeBrowser[Node] {

    import scala.collection.JavaConversions._

    def parent(node: Node) = node.getParentNode

    def document(node: Node) = node.getOwnerDocument

    def children(node: Node) = {
        val children = node.getChildNodes
        (0 until children.getLength).map({i => children.item(i)})
    }

    def isElement(node: Node) = node.isInstanceOf[Element]

    def namespaceUri(node: Node) = node.getNamespaceURI

    def name(node: Node) = node match {
        case e: Element => e.getTagName
    }

    def attribute(node: Node, uri: String, name: String) = node match {
        case e: Element => if (uri == null) e.getAttribute(name) else e.getAttributeNS(uri, name)
    }

}
