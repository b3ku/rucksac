package org.rucksac

import scala.xml
import xml.{PCData, Text, Elem}
import java.lang.IllegalArgumentException
import collection.{immutable, Seq}

/**
 * @author Andreas Kuhrwahl
 * @since 28.09.12
 */
case class ScalaXmlNode(n: xml.Node, p: xml.Node = null) extends Node {
    self =>

    type Orig = xml.Node

    def companion = ScalaXmlNode

    def apply() = n

    val _parent   = p
    val _children = n.child

    override def children: Seq[Node] = {
        val children = _children
        new immutable.IndexedSeq[Node] {
            def length = children.size

            def apply(idx: Int) = new ScalaXmlNode(n, self()).asInstanceOf[Node]
        }
    }

    def attribute(uri: String, name: String) = n match {
        case e: Elem => {
            val attr = (if (uri == null) e.attribute(name) else e.attribute(uri, name)).getOrElse(null)
            if (attr != null) attr.toString() else null
        }
        case _ => throw new IllegalArgumentException(n.toString())
    }

    def text = n match {
        case t: Text => t.data
        case d: PCData => d.data
        case _ => throw new IllegalArgumentException(n.toString())
    }

    def name = n match {
        case e: Elem => e.label
        case _ => throw new IllegalArgumentException(n.toString())
    }

    def namespaceUri = n.namespace

    def isElement = n.isInstanceOf[Elem]

    def isText = n.isInstanceOf[Text] || n.isInstanceOf[PCData]

}

object ScalaXmlNode extends NodeFactory[xml.Node] {

    implicit def asNode(node: xml.Node) = new ScalaXmlNode(node)

}
