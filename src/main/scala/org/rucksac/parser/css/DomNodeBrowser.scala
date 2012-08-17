package org.rucksac.parser.css

import org.rucksac.NodeBrowser
import org.w3c.dom.Node

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */
class DomNodeBrowser extends NodeBrowser[Node] {

    import scala.collection.JavaConversions._

    def children(node: Node) = {
        val children = node.getChildNodes
        (0 until children.getLength).map({i => children.item(i)})
    }

}
