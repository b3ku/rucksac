package org.rucksac.parser.css

import org.w3c.dom
import org.rucksac.{ScalaXmlNode, DomNode}
import $._
import scala.collection.JavaConversions._

/**
 * @author Andreas Kuhrwahl
 * @since 01.09.12
 */
object Query4J {

    def queryDom(q: String, nodes: java.util.List[dom.Node]): java.lang.Iterable[dom.Node] = new
            Query(nodes)({n: dom.Node => DomNode.asNode(n)}).findAll(q)

    def queryScalaNodes(q: String, nodes: java.util.List[xml.Node]): java.lang.Iterable[xml.Node] = new
            Query(nodes)({n: xml.Node => ScalaXmlNode.asNode(n)}).findAll(q)

}
