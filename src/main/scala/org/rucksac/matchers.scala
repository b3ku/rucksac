package org.rucksac

import org.rucksac.utils._

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */
package object matchers {

    private object button extends PseudoClassMatcher {
        def apply[T](node: T, browser: NodeBrowser[T]) = browser.isElement(node) && (browser.name(node) == "button" ||
            (browser.name(node) == "input" && attribute(node, browser, "type") == "button"))
    }

    private object neOp extends AttributeOperationMatcher {
        def apply[T](node: T, browser: NodeBrowser[T], uri: String, name: String, value: String) = {
            val attrValue = browser.attribute(node, uri, name)
            attrValue == null || attrValue != value
        }
    }

    object jQueryMatcherRegistrar extends NodeMatcherRegistrar {
        def registerNodeMatchers(registry: NodeMatcherRegistry) {
            registry.registerPseudoClassMatcher("button", button)
            registry.registerAttributeOperationMatcher("!=", neOp)

            //TODO :eq()
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

}
