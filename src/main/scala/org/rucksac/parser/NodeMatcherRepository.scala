package org.rucksac.parser

import org.rucksac.NodeMatcher
import collection.mutable.ListBuffer

/**
 * @author Andreas Kuhrwahl
 * @since 21.08.12
 */
object NodeMatcherRepository {

    val pseudoClassNodeMatchers = new ListBuffer[NodeMatcher]

    pseudoClassNodeMatchers += new PseudoClass("..")

}
