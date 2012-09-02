package org.rucksac.parser

import org.rucksac.Node

/**
 * @author Andreas Kuhrwahl
 * @since 01.09.12
 */
trait Matchable {def apply[T](node: Node[T]): Boolean}
