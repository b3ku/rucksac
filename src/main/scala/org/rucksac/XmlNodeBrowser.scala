package org.rucksac

import scala.xml.Elem
import scala.xml.Text
import scala.xml.PCData
import scala.collection.mutable

/**
 * @author Oliver Becker
 * @since 26.08.12
 */
class XmlNodeBrowser extends NodeBrowser[scala.xml.Node] {

    private var parentMap = mutable.HashMap[scala.xml.Node, scala.xml.Node]()

    def parent(node: scala.xml.Node) = parentMap get node getOrElse null

    def children(node: scala.xml.Node) = {
        node.child foreach {c => parentMap.put(c, node)}
        node.child
    }

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
