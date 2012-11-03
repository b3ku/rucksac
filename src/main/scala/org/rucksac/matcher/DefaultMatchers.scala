package org.rucksac.matcher

import org.rucksac._

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */

private object buttonClass extends PseudoClassMatcher {
    def apply(node: Node) = node.isElement &&
            (node.name == "button" || (node.name == "input" && node.attribute("type") == "button"))
}

private object neOp extends AttributeOperationMatcher {
    def apply(attributeValue: String, operationValue: String) = {
        attributeValue == null || attributeValue != operationValue
    }
}

object DefaultMatchers extends NodeMatcherRegistrar {
    def registerNodeMatchers(registry: NodeMatcherRegistry) {
        registry.pseudoClasses("button") = buttonClass
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
