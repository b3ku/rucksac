package org.rucksac.parser

import org.rucksac.NodeBrowser
import scala.xml.{PCData, Text, Elem, Node}
import org.rucksac.matchers._
import scala.xml.Text
import scala.xml.PCData
import scala.collection.mutable

/**
 * @author Oliver Becker
 * @since 26.08.12
 */
class XmlNodeBrowser extends NodeBrowser[Node] {

    import scala.collection.JavaConversions._

    all(this)

    private var parentMap = mutable.HashMap[Node,Node]()

    def parent(node: Node) = parentMap get node getOrElse null

    def children(node: Node) = {
        node.child foreach { c => parentMap.put(c, node) }
        node.child
    }

    def isElement(node: Node) = node.isInstanceOf[Elem]

    def isText(node: Node) = node.isInstanceOf[Text] || node.isInstanceOf[PCData]

    def text(node: Node) = node match {
        case t: Text => t.data
        case d: PCData => d.data
        case _ => throw new IllegalArgumentException(node.toString())
    }

    def name(node: Node) = node match {
        case e: Elem => e.label
        case _ => throw new IllegalArgumentException(node.toString())
    }

    def namespaceUri(node: Node) = node match {
        case e: Elem => e.namespace
        case _ => throw new IllegalArgumentException(node.toString())
    }

    def attribute(node: Node, uri: String, name: String) = node match {
        case e: Elem => {
            val attr = (if (uri == null) e.attribute(name) else e.attribute(uri, name)).getOrElse(null)
            if (attr != null) attr.toString() else null
        }
        case _ => throw new IllegalArgumentException(node.toString())
    }
}
