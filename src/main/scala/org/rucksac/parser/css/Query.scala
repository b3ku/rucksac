package org.rucksac.parser.css

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */

case class Query[T](q: String) {

    import scala.collection.JavaConversions._

    val selectors = new Parser().parse(Option(q).orElse(Option("")).get)

    def filter(node: T): java.lang.Iterable[T] = this.selectors.filter(node)

}

object $ {def apply[T](q: String) = new Query[T](q)}
