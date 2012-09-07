package org.rucksac.parser

import org.rucksac.Node

/**
 * @author Oliver Becker
 * @since 07.09.12
 */
trait ListMatchable extends Matchable {

    override def apply[T](nodes: List[Node[T]]): List[Node[T]] = nodes filter {apply(_)}

}
