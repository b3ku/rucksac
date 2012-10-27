package org.rucksac.matcher

import org.rucksac._

/**
 * @author Andreas Kuhrwahl
 * @author Oliver Becker
 * @since 30.08.12
 */

private object buttonClass extends SimplePseudoClassMatcher {
    def apply[T](node: Node[T]) = node.isElement &&
            (node.name == "button" || (node.name == "input" && node.attribute("type") == "button"))
}

private object submitClass extends SimplePseudoClassMatcher {
    def apply[T](node: Node[T]) = node.isElement &&
            (node.name == "button" || node.name == "input") && node.attribute("type") == "submit"
}

private object evenClass extends PositionalPseudoClassMatcher {
    def apply[T](node: Node[T], nodes: Seq[Node[T]]) = nodes.indexOf(node) % 2 == 0
}

private object oddClass extends PositionalPseudoClassMatcher {
    def apply[T](node: Node[T], nodes: Seq[Node[T]]) = nodes.indexOf(node) % 2 == 1
}

private object firstClass extends PositionalPseudoClassMatcher {
    def apply[T](node: Node[T], nodes: Seq[Node[T]]) = node == nodes.head
}

private object lastClass extends PositionalPseudoClassMatcher {
    def apply[T](node: Node[T], nodes: Seq[Node[T]]) = node == nodes.last
}

private class indexBasedFunc(comp: (Int, Int) => Boolean) extends PositionalPseudoFunctionMatcher {
    def apply[T](node: Node[T], nodes: Seq[Node[T]], exp: String) = {
        try {
            comp(nodes.indexOf(node), exp.toInt)
        } catch {
            case _: NumberFormatException => throw new ParseException(exp)
        }
    }
}

private object eqFunc extends indexBasedFunc(_ == _)

private object gtFunc extends indexBasedFunc(_ > _)

private object ltFunc extends indexBasedFunc(_ < _)

private object neOp extends AttributeOperationMatcher {
    def apply[T](attributeValue: String, operationValue: String) = {
        attributeValue == null || attributeValue != operationValue
    }
}

object jQueryMatcherRegistrar extends matcher.NodeMatcherRegistrar {
    def registerNodeMatchers(registry: NodeMatcherRegistry) {
        registry.simplePseudoClasses("button") = buttonClass
        registry.simplePseudoClasses("submit") = submitClass
        registry.positionalPseudoClasses("even") = evenClass
        registry.positionalPseudoClasses("odd") = oddClass
        registry.positionalPseudoClasses("first") = firstClass
        registry.positionalPseudoClasses("last") = lastClass
        registry.positionalPseudoFunctions("eq") = eqFunc
        registry.positionalPseudoFunctions("gt") = gtFunc
        registry.positionalPseudoFunctions("lt") = ltFunc
        registry.attributeOperations("!=") = neOp

        //TODO :has(selector)
        //TODO :checkbox
        //TODO :file
        //TODO :header
        //TODO :hidden
        //TODO :image
        //TODO :input
        //TODO :parent
        //TODO :password
        //TODO :radio
        //TODO :reset
        //TODO :selected
        //TODO :text
        //TODO :visible
    }
}
