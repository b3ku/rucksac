package org.rucksac

import org.w3c.dom
import dom.{Text, Element}
import java.lang.IllegalArgumentException

/**
 * @author Andreas Kuhrwahl
 * @since 27.09.12
 */
case class DomNode(n: dom.Node) extends Node {

    type Orig = dom.Node

    def apply() = n

    def companion = DomNode

    val _parent = n.getParentNode match {
        case e: Element => e
        case _ => null
    }

    val _children = {
        val children = n.getChildNodes
        (0 until children.getLength) map {children.item(_)}
    }

    def attribute(uri: String, name: String) = n match {
        case e: Element => {
            val attr = if (uri == null) e.getAttributeNode(name) else e.getAttributeNodeNS(uri, name)
            if (attr == null) null else attr.getValue
        }
        case _ => throw new IllegalArgumentException(n.toString)
    }

    def text = n match {
        case t: Text => t.getWholeText
        case _ => throw new IllegalArgumentException(n.toString)
    }

    def name = n match {
        case e: Element => if (e.getLocalName == null) e.getTagName else e.getLocalName
        case _ => throw new IllegalArgumentException(n.toString)
    }

    def namespaceUri = n.getNamespaceURI

    def isElement = n.isInstanceOf[Element]

    def isText = n.isInstanceOf[Text]

}

object DomNode extends NodeFactory[dom.Node] {

    implicit def asNode(n: dom.Node) = new DomNode(n)

}


