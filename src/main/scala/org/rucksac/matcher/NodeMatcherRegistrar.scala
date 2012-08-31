package org.rucksac.matcher

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */
trait NodeMatcherRegistrar {

    def registerNodeMatchers(registry: NodeMatcherRegistry)

}
