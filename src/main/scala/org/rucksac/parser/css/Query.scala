package org.rucksac.parser.css

import org.rucksac.NodeBrowser

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */

private class nodeBrowser[T] extends (() => NodeBrowser[T]) {

    val NODE_BROWSER         = classOf[NodeBrowser[T]].getName
    val DEFAULT_NODE_BROWSER = Option(classOf[DomNodeBrowser].getName)

    val nodeBrowserType = sys.props.get(NODE_BROWSER).orElse(DEFAULT_NODE_BROWSER).get

    def apply(): NodeBrowser[T] = Class.forName(nodeBrowserType).newInstance().asInstanceOf[NodeBrowser[T]]

}

case class Query[T](q: Option[String]) {

    import scala.collection.JavaConversions._

    val selectors               = Parser.parse(q.orElse(Option("")).get)
    val browser: NodeBrowser[T] = new nodeBrowser[T]()()

    def filter(node: T): java.lang.Iterable[T] = this.selectors.filter(node, this.browser)

}

object Query {def $[T](query: String) = Query[T](Option(query))}
