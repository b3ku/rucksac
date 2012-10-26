package org.rucksac

import scala.xml.Elem
import scala.xml.Text
import scala.xml.PCData
import scala.xml.Document

/**
 * @author Oliver Becker
 * @since 26.08.12
 */
class XmlNodeBrowser extends NodeBrowser[scala.xml.Node] {

    def children(node: scala.xml.Node) = node.child

    def isDocument(node: scala.xml.Node) = node.isInstanceOf[Document]

    def isElement(node: scala.xml.Node) = node.isInstanceOf[Elem]

    def isText(node: scala.xml.Node) = node.isInstanceOf[Text] || node.isInstanceOf[PCData]

    def text(node: scala.xml.Node) = node match {
        case t: Text => t.data
        case d: PCData => d.data
        case _ => throw new IllegalArgumentException(node.toString())
    }

    def name(node: scala.xml.Node) = node match {
        case e: Elem => e.label
        case _ => throw new IllegalArgumentException(node.toString())
    }

    def namespaceUri(node: scala.xml.Node) = node.namespace

    def attribute(node: scala.xml.Node, uri: String, name: String) = node match {
        case e: Elem => {
            val attr = (if (uri == null) e.attribute(name) else e.attribute(uri, name)).getOrElse(null)
            if (attr != null) attr.toString() else null
        }
        case _ => throw new IllegalArgumentException(node.toString())
    }

}

object XmlNodeBrowser {

    val instance = new XmlNodeBrowser

}
