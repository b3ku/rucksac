package org.rucksac.parser

import org.rucksac.NodeBrowser

/**
 * @author Andreas Kuhrwahl
 * @since 22.08.12
 */
trait Matchable {def apply[T](node: T, browser: NodeBrowser[T]): Boolean}
