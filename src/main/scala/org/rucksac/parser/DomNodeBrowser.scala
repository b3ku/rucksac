package org.rucksac.parser

import org.rucksac.NodeBrowser
import org.rucksac.matchers._
import org.w3c.dom.{Text, Element, Node}

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */
class DomNodeBrowser extends NodeBrowser[Node] {

    import scala.collection.JavaConversions._

    all(this);

    def parent(node: Node) = node.getParentNode match {
        case el: Element => el
        case _ => null // Document
    }

    def children(node: Node) = node match {
        case e: Element => {
            val children = e.getChildNodes
            (0 until children.getLength).map({i => children.item(i)})
        }
        case _ => throw new IllegalArgumentException(node.toString)
    }

    def isElement(node: Node) = node.isInstanceOf[Element]

    def isText(node: Node) = node.isInstanceOf[Text]

    def text(node: Node) = node match {
        case t: Text => t.getWholeText
        case _ => throw new IllegalArgumentException(node.toString)
    }

    def name(node: Node) = node match {
        case e: Element => if (e.getLocalName == null) e.getTagName else e.getLocalName
        case _ => throw new IllegalArgumentException(node.toString)
    }

    def namespaceUri(node: Node) = node match {
        case e: Element => e.getNamespaceURI
        case _ => throw new IllegalArgumentException(node.toString)
    }

    def attribute(node: Node, uri: String, name: String) = node match {
        case e: Element => {
            val attr = if (uri == null) e.getAttributeNode(name) else e.getAttributeNodeNS(uri, name)
            if (attr == null) null else attr.getValue
        }
        case _ => throw new IllegalArgumentException(node.toString)
    }

}
