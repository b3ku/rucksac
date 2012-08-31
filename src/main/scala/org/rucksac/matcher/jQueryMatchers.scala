package org.rucksac.matcher

import org.rucksac._

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */

private object buttonClass extends PseudoClassMatcher {
    def apply[T](node: T, nodes: Seq[T]) = node.isElement() &&
        (node.name() == "button" || (node.name() == "input" && node.attribute("type") == "button"))
}

private object eqFunc extends PseudoFunctionMatcher {
    def apply[T](node: T, nodes: Seq[T], exp: String) = {
        try {
            nodes(exp.toInt - 1) == node
        } catch {
            case _: NumberFormatException | _: IndexOutOfBoundsException => false // TODO logging?
        }
    }
}

private object neOp extends AttributeOperationMatcher {
    def apply[T](node: T, uri: String, name: String, value: String) = {
        val attrValue = node.attribute(uri, name)
        attrValue == null || attrValue != value
    }
}

object jQueryMatcherRegistrar extends matcher.NodeMatcherRegistrar {
    def registerNodeMatchers(registry: NodeMatcherRegistry) {
        registry.pseudoClasses("button") = buttonClass
        registry.pseudoFunctions("eq") = eqFunc
        registry.attributeOperations("!=") = neOp

        //TODO :gt()
        //TODO :lt()
        //TODO :even
        //TODO :odd
        //TODO :first
        //TODO :last
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
        //TODO :submit
        //TODO :text
        //TODO :visible
    }
}