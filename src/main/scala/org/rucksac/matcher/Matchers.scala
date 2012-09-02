package org.rucksac.matcher

import org.rucksac.Node

/**
 * @author Andreas Kuhrwahl
 * @since 31.08.12
 */

trait PseudoClassMatcher {def apply[T](node: Node[T], nodes: Seq[Node[T]]): Boolean}

trait PseudoFunctionMatcher {def apply[T](node: Node[T], nodes: Seq[Node[T]], exp: String): Boolean}

trait SelectorCombinatorMatcher {def apply[T](node: Node[T]): Boolean}

trait AttributeOperationMatcher {def apply[T](node: Node[T], uri: String, name: String, value: String): Boolean}
