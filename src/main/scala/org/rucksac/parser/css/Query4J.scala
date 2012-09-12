package org.rucksac.parser.css

import org.rucksac.Node

/**
 * @author Andreas Kuhrwahl
 * @since 01.09.12
 */
object Query4J {

    import scala.collection.JavaConversions._

    def $[T](q: String, node: T): java.lang.Iterable[T] = Query(q, node)()

}
