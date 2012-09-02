package org.rucksac.matcher

import org.rucksac._

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */

private object buttonClass extends PseudoClassMatcher {
    def apply[T](node: Node[T], nodes: Seq[Node[T]]) = node.isElement &&
            (node.name == "button" || (node.name == "input" && node.attribute("type") == "button"))
}

private class indexBasedFunc(comp: (Int, Int) => Boolean) extends PseudoFunctionMatcher {
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
    def apply[T](node: Node[T], uri: String, name: String, value: String) = {
        val attrValue = node.attribute(uri, name)
        attrValue == null || attrValue != value
    }
}

object jQueryMatcherRegistrar extends matcher.NodeMatcherRegistrar {
    def registerNodeMatchers(registry: NodeMatcherRegistry) {
        registry.pseudoClasses("button") = buttonClass
        registry.pseudoFunctions("eq") = eqFunc
        registry.pseudoFunctions("gt") = gtFunc
        registry.pseudoFunctions("lt") = ltFunc
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