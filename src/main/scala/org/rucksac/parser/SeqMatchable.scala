package org.rucksac.parser

import org.rucksac.Node

/**
 * @author Oliver Becker
 * @since 07.09.12
 */
trait SeqMatchable extends Matchable {

    override def apply[T](nodes: Seq[Node[T]]): Seq[Node[T]] = nodes filter {apply(_)}

}
