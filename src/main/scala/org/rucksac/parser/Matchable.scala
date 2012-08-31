package org.rucksac.parser

/**
 * @author Andreas Kuhrwahl
 * @since 22.08.12
 */
trait Matchable {

    def apply[T](nodes: Seq[T]): Seq[T]

}
