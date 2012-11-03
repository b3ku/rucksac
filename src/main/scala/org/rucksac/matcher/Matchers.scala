package org.rucksac.matcher

import org.rucksac.Node

/**
 * @author Andreas Kuhrwahl
 * @since 31.08.12
 */

trait PseudoClassMatcher extends (Node => Boolean)

trait PseudoFunctionMatcher {def apply(node: Node, exp: String): Boolean}

trait SelectorCombinatorMatcher extends (Node => Boolean)

trait AttributeOperationMatcher {def apply(attributeValue: String, operationValue: String): Boolean}
