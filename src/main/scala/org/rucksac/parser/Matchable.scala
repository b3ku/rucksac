package org.rucksac.parser

import org.rucksac.Node

/**
 * @author Andreas Kuhrwahl
 * @since 01.09.12
 */
trait Matchable {

    def apply[T](node: Node[T]): Boolean

    def apply[T](nodes: Seq[Node[T]]): Seq[Node[T]]

    val mustFilter: Boolean

}
