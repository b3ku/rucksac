package org.rucksac.matcher

import org.rucksac.Node
import org.rucksac.parser.Matchable

/**
 * @author Andreas Kuhrwahl
 * @author Oliver Becker
 * @since 31.08.12
 */

/**
 * Base trait for the implementation of simple extension pseudo class matchers. ''Simple'' means that no information of
 * other matching nodes is required for the evaluation.
 */
trait SimplePseudoClassMatcher {

    /**
     * Matches a node.
     * @param node the current node to evaluate
     * @tparam T the type of the wrapped node
     * @return `true` if the given `node` matches this pseudo class
     */
    def apply[T](node: Node[T]): Boolean

}

/**
 * Base trait for the implementation of positional extension pseudo class matchers.
 */
trait PositionalPseudoClassMatcher {

    /**
     * Matches a node in a sequence of nodes.
     * @param node the current node to evaluate
     * @param nodes the sequence of all candidate nodes for this pseudo class (so `node` will be contained in `nodes`)
     * @tparam T the type of the wrapped node
     * @return `true` if the given `node` matches this pseudo class
     */
    def apply[T](node: Node[T], nodes: Seq[Node[T]]): Boolean

}

/**
 * Base trait for the implementation of simple extension pseudo function matchers. ''Simple'' means that no information
 * of other matching nodes is required for the evaluation.
 */
trait SimplePseudoFunctionMatcher {

    /**
     * Matches a node.
     * @param node the current node to evaluate
     * @param exp the parameter to the pseudo function as a string
     * @tparam T the type of the wrapped node
     * @return `true` if the given `node` matches this pseudo function
     */
    def apply[T](node: Node[T], exp: String): Boolean

}

/**
 * Base trait for the implementation of positional extension pseudo function matchers.
 */
trait PositionalPseudoFunctionMatcher {

    /**
     * Matches a node in a sequence of nodes.
     * @param node the current node to evaluate
     * @param nodes the sequence of all candidate nodes for this pseudo class (so `node` will be contained in `nodes`)
     * @param exp the parameter to the pseudo function as a string
     * @tparam T the type of the wrapped node
     * @return `true` if the given `node` matches this pseudo function
     */
    def apply[T](node: Node[T], nodes: Seq[Node[T]], exp: String): Boolean

}

trait SelectorCombinatorMatcher extends Matchable

/**
 * Base trait for the implementation of extension attribute operator matchers.
 */
trait AttributeOperationMatcher {

    /**
     * Matches an attribute value.
     * @param attributeValue the attribute value of the node to evaluate
     * @param operationValue the supplied value after the operator
     * @tparam T the type of the wrapped node
     * @return `true` if the attribute value matches the operation value
     */
    def apply[T](attributeValue: String, operationValue: String): Boolean

}
