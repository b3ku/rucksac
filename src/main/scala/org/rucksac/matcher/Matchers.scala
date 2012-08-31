package org.rucksac.matcher

/**
 * @author Andreas Kuhrwahl
 * @since 31.08.12
 */

trait PseudoClassMatcher {def apply[T](node: T, nodes: Seq[T]): Boolean}

trait PseudoFunctionMatcher {def apply[T](node: T, nodes: Seq[T], exp: String): Boolean}

trait SelectorCombinatorMatcher {def apply[T](node: T): Boolean}

trait AttributeOperationMatcher {def apply[T](node: T, uri: String, name: String, value: String): Boolean}
