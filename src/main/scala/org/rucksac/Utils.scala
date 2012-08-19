package org.rucksac

/**
 * @author Andreas Kuhrwahl
 * @since 19.08.12
 */

package object utils {

    def matchesAnyParent[T](node: T, browser: NodeBrowser[T], matches: T => Boolean): Boolean = {
        var result = false
        val parent = browser.parent(node);
        if (parent != null && parent != browser.document(node)) {
            result = matches(parent)
            if (!result) {
                result = matchesAnyParent(parent, browser, matches)
            }
        }
        return result
    }

}