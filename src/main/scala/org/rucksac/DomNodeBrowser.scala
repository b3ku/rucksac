package org.rucksac

import org.w3c.dom.{Document, Text, Element}

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */
class DomNodeBrowser extends NodeBrowser[org.w3c.dom.Node] {

    def children(node: org.w3c.dom.Node) = {
        val children = node.getChildNodes
        (0 until children.getLength) map { children.item(_) }
    }

    def isDocument(node: org.w3c.dom.Node) = node.isInstanceOf[Document]

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

object DomNodeBrowser {

    val instance = new DomNodeBrowser

}
