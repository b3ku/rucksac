package org.rucksac

import org.w3c.dom.{Text, Element}

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */
class DomNodeBrowser extends NodeBrowser[org.w3c.dom.Node] {

    def parent(node: org.w3c.dom.Node) = node.getParentNode match {
        case el: Element => el
        case _ => null // Document
    }

    def children(node: org.w3c.dom.Node) = {
        val children = node.getChildNodes
        (0 until children.getLength).map({i => children.item(i)})
    }

    def isElement(node: org.w3c.dom.Node) = node.isInstanceOf[Element]

    def isText(node: org.w3c.dom.Node) = node.isInstanceOf[Text]

    def text(node: org.w3c.dom.Node) = node match {
        case t: Text => t.getWholeText
        case _ => throw new IllegalArgumentException(node.toString)
    }

    def name(node: org.w3c.dom.Node) = node match {
        case e: Element => if (e.getLocalName == null) e.getTagName else e.getLocalName
        case _ => throw new IllegalArgumentException(node.toString)
    }

    def namespaceUri(node: org.w3c.dom.Node) = node.getNamespaceURI

    def attribute(node: org.w3c.dom.Node, uri: String, name: String) = node match {
        case e: Element => {
            val attr = if (uri == null) e.getAttributeNode(name) else e.getAttributeNodeNS(uri, name)
            if (attr == null) null else attr.getValue
        }
        case _ => throw new IllegalArgumentException(node.toString)
    }

}
