package org.rucksac.parser

import org.rucksac.NodeBrowser

/**
 * @author Andreas Kuhrwahl
 * @since 12.08.12
 */

protected trait Matchable {
    def matches[T](node: T, browser: NodeBrowser[T]): Boolean
}
