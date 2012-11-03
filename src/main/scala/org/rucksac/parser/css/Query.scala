package org.rucksac.parser.css

import collection._
import org.rucksac.{ScalaXmlNode, DomNode, Node}
import org.w3c.dom

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */
package object $ {

    implicit def domNode2Node(node: dom.Node): Node = DomNode.asNode(node)

    implicit def xmlScalaNode2Node(node: xml.Node): Node = ScalaXmlNode.asNode(node)

    class Query[N <% Node](nodes: Iterable[N]) {

        def findAll(p: N => Boolean) = {
            val b = new mutable.ArrayBuffer[N]()
            def iterate(node: N) {
                node.children foreach {
                    c =>
                        val child: N = c().asInstanceOf[N]
                        if (p(child)) {
                            b += child
                        }
                        iterate(child)
                }
            }
            nodes foreach {iterate(_)}
            b.result()
        }

    }

    implicit def augmentNodes[N <% Node](nodes: Iterable[N]): Query[N] = new Query(nodes)

    implicit def asPredicate[N <% Node](sel: String): N => Boolean = {
        val selectors = new Parser().parse(sel)
        (node => (false /: selectors)(_ || _(node)))
    }

}
