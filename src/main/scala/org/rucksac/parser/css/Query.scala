package org.rucksac.parser.css

import org.rucksac.NodeBrowser
import org.rucksac.parser.DomNodeBrowser

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */

object NodeBrowserFactory {

    val NODE_BROWSER         = getClass.getName + ".nodeBrowser"
    val DEFAULT_NODE_BROWSER = Option(classOf[DomNodeBrowser].getName)

    val nodeBrowserType = sys.props.get(NODE_BROWSER).orElse(DEFAULT_NODE_BROWSER).get
    val nodeBrowser     = Class.forName(nodeBrowserType).newInstance()

    def apply[T]() = nodeBrowser.asInstanceOf[NodeBrowser[T]]

}

case class Query[T](q: String) {

    import scala.collection.JavaConversions._

    val selectors = new Parser(NodeBrowserFactory[T]()).parse(Option(q).orElse(Option("")).get)

    def filter(node: T): java.lang.Iterable[T] = this.selectors.filter(node, NodeBrowserFactory[T]())

}

object $ {def apply[T](q: String) = new Query[T](q)}
