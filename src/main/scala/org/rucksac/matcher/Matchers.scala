package org.rucksac.matcher

import org.rucksac.Node
import org.rucksac.parser.Matchable

/**
 * @author Andreas Kuhrwahl
 * @since 31.08.12
 */

trait PseudoClassMatcher {

    def apply[T](node: Node[T]): Boolean

}

trait PseudoFunctionMatcher {

    def apply[T](node: Node[T], nodes: Seq[Node[T]], exp: String): Boolean

}

trait SelectorCombinatorMatcher extends Matchable

trait AttributeOperationMatcher {

    def apply[T](attributeValue: String, operationValue: String): Boolean

}
