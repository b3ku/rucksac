package org.rucksac.matcher

import collection.mutable
import org.rucksac.ParseException

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */
class NodeMatcherRegistry {

    val pseudoClasses = new
                    mutable.HashMap[String, PseudoClassMatcher]() with mutable.SynchronizedMap[String, PseudoClassMatcher] {
        override def default(key: String) = throw new ParseException(":" + key + " not supported")
    }

    val pseudoFunctions = new
                    mutable.HashMap[String, PseudoFunctionMatcher]() with mutable.SynchronizedMap[String, PseudoFunctionMatcher] {
        override def default(key: String) = throw new ParseException(":" + key + "() not supported")
    }

    val attributeOperations = new
                    mutable.HashMap[String, AttributeOperationMatcher]() with mutable.SynchronizedMap[String, AttributeOperationMatcher]

    val selectorCombinators = new
                    mutable.HashMap[String, SelectorCombinatorMatcher]() with mutable.SynchronizedMap[String, SelectorCombinatorMatcher]

}

object NodeMatcherRegistry {

    private val nodeMatcherRegistry = new NodeMatcherRegistry
    DefaultMatchers.registerNodeMatchers(nodeMatcherRegistry)

    def apply() = nodeMatcherRegistry

}
