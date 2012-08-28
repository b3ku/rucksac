package org.rucksac

import collection.mutable
import java.util.Collections

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */

package object utils {

    import scala.collection.JavaConverters._

    def parent[T](node: T, browser: NodeBrowser[T]) = Option(browser.parent(node))

    def children[T](node: T, browser: NodeBrowser[T]): List[T] =
        if (browser.isElement(node)) browser.children(node).asScala.toList else List[T]()

    def attribute[T](node: T, browser: NodeBrowser[T], uri: String, name: String): String =
        Option(browser.attribute(node, uri, name)).getOrElse("")

    def attribute[T](node: T, browser: NodeBrowser[T], name: String): String = attribute(node, browser, null, name)

    def text[T](node: T, browser: NodeBrowser[T]) = Option(browser.text(node)).getOrElse("")

    def namespaceUri[T](node: T, browser: NodeBrowser[T]) = Option(browser.namespaceUri(node)).getOrElse("")

    def textNodes[T](nodes: Iterable[T], browser: NodeBrowser[T]): Iterable[String] =
        nodes filter {browser.isText(_)}  map {browser.text(_)}

    def matchesAnyParent[T](node: T, browser: NodeBrowser[T], matches: T => Boolean): Boolean = {
        val parent = browser.parent(node)
        parent != null && (matches(parent) || matchesAnyParent(parent, browser, matches))
    }

    def siblings[T](node: T, browser: NodeBrowser[T]) = parent(node, browser) map
        { children(_, browser) filter {browser.isElement(_)} } getOrElse (List(node))

    def siblingsOfSameType[T](node: T, browser: NodeBrowser[T]) = {
        val expName = (browser.name(node), browser.namespaceUri(node))
        siblings(node, browser).filter(el =>
            browser.isElement(el) && (browser.name(el), browser.namespaceUri(el)) == expName)
    }

}

package object matchers {

    import utils._

    private object buttonClass extends PseudoClassMatcher {
        def apply[T](node: T, nodes: java.util.List[T], browser: NodeBrowser[T]) =
            browser.isElement(node) && (browser.name(node) == "button" ||
            (browser.name(node) == "input" && attribute(node, browser, "type") == "button"))
    }

    private class indexBasedFunc(comp: (Int, Int) => Boolean) extends PseudoFunctionMatcher {
        def apply[T](node: T, nodes: java.util.List[T], browser: NodeBrowser[T], exp: String) = {
            try {
                comp(nodes.indexOf(node), exp.toInt)
            } catch {
                case _: NumberFormatException=> throw new ParseException(exp)
            }
        }
    }

    private object eqFunc extends indexBasedFunc(_ == _)
    private object gtFunc extends indexBasedFunc(_ > _)
    private object ltFunc extends indexBasedFunc(_ < _)

    private object neOp extends AttributeOperationMatcher {
        def apply[T](node: T, browser: NodeBrowser[T], uri: String, name: String, value: String) = {
            val attrValue = browser.attribute(node, uri, name)
            attrValue == null || attrValue != value
        }
    }

    object jQueryMatcherRegistrar extends NodeMatcherRegistrar {
        def registerNodeMatchers(registry: NodeMatcherRegistry) {
            registry.registerPseudoClassMatcher("button", buttonClass)
            registry.registerPseudoFunctionMatcher("eq", eqFunc)
            registry.registerPseudoFunctionMatcher("gt", gtFunc)
            registry.registerPseudoFunctionMatcher("lt", ltFunc)
            registry.registerAttributeOperationMatcher("!=", neOp)

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

    def all(registry: NodeMatcherRegistry) = {
        jQueryMatcherRegistrar.registerNodeMatchers(registry)
    }

}

