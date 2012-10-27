package org.rucksac.matcher

import collection.mutable
import org.rucksac.ParseException

/**
 * @author Andreas Kuhrwahl
 * @author Oliver Becker
 * @since 30.08.12
 */
class NodeMatcherRegistry {

    val simplePseudoClasses = new
            mutable.HashMap[String, SimplePseudoClassMatcher]() with mutable.SynchronizedMap[String, SimplePseudoClassMatcher] {
        override def default(key: String) = throw new ParseException(":" + key + " not supported")
    }

    val positionalPseudoClasses = new
            mutable.HashMap[String, PositionalPseudoClassMatcher]() with mutable.SynchronizedMap[String, PositionalPseudoClassMatcher] {
        override def default(key: String) = throw new ParseException(":" + key + " not supported")
    }

    val simplePseudoFunctions = new
            mutable.HashMap[String, SimplePseudoFunctionMatcher]() with mutable.SynchronizedMap[String, SimplePseudoFunctionMatcher] {
        override def default(key: String) = throw new ParseException(":" + key + "() not supported")
    }

    val positionalPseudoFunctions = new
            mutable.HashMap[String, PositionalPseudoFunctionMatcher]() with mutable.SynchronizedMap[String, PositionalPseudoFunctionMatcher] {
        override def default(key: String) = throw new ParseException(":" + key + "() not supported")
    }

    val attributeOperations = new
            mutable.HashMap[String, AttributeOperationMatcher]() with mutable.SynchronizedMap[String, AttributeOperationMatcher]

    val selectorCombinators = new
            mutable.HashMap[String, SelectorCombinatorMatcher]() with mutable.SynchronizedMap[String, SelectorCombinatorMatcher]

}

object NodeMatcherRegistry {

    private val nodeMatcherRegistry = new NodeMatcherRegistry

    def apply() = nodeMatcherRegistry

    def jquery() = {
        jQueryMatcherRegistrar.registerNodeMatchers(nodeMatcherRegistry)
        this
    }

    def all() = {
        jquery()
    }

}
