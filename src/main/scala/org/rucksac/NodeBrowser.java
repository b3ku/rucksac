/*
 * Catapult Framework Project
 *
 * Copyright (c) 2002-2012 CatapultSource.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.rucksac;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interface for traversing a hierarchical node tree (having nodes of type <code>T</code>) that represents a HTML or XML
 * data structure.
 *
 * @author Andreas Kuhrwahl
 * @since 12.08.12
 */
public abstract class NodeBrowser<T> implements NodeMatcherRegistry {

    private final Map<String, PseudoClassMatcher> pseudoClasses = new ConcurrentHashMap<String, PseudoClassMatcher>();
    private final Map<String, PseudoFunctionMatcher> pseudoFunctions =
            new ConcurrentHashMap<String, PseudoFunctionMatcher>();
    private final Map<String, AttributeOperationMatcher> attributeOperations =
            new ConcurrentHashMap<String, AttributeOperationMatcher>();
    private final Map<String, SelectorCombinatorMatcher> selectorCombinators =
            new ConcurrentHashMap<String, SelectorCombinatorMatcher>();

    @Override
    public void registerPseudoClassMatcher(String pattern, PseudoClassMatcher matcher) {
        this.pseudoClasses.put(pattern, matcher);
    }

    @Override
    public PseudoClassMatcher findPseudoClassMatcher(String name) {
        //TODO patternmatcher
        PseudoClassMatcher matcher = this.pseudoClasses.get(name);
        if (matcher == null) {
            throw new PseudoClassNotSupportedException(name);
        }
        return matcher;
    }

    @Override
    public void registerAttributeOperationMatcher(String op, AttributeOperationMatcher matcher) {
        this.attributeOperations.put(op, matcher);
    }

    @Override
    public AttributeOperationMatcher findAttributeOperationMatcher(String op) {
        return this.attributeOperations.get(op);
    }

    @Override
    public String[] getSupportedAttributeOperations() {
        return this.attributeOperations.keySet().toArray(new String[this.attributeOperations.size()]);
    }

    @Override
    public void registerPseudoFunctionMatcher(String pattern, PseudoFunctionMatcher matcher) {
        this.pseudoFunctions.put(pattern, matcher);
    }

    @Override
    public PseudoFunctionMatcher findPseudoFunctionMatcher(String name) {
        //TODO patternmatcher
        PseudoFunctionMatcher matcher = this.pseudoFunctions.get(name);
        if (matcher == null) {
            throw new PseudoFunctionNotSupportedException(name);
        }
        return matcher;
    }

    @Override
    public void registerSelectorCombinatorMatcher(String op, SelectorCombinatorMatcher matcher) {
        this.selectorCombinators.put(op, matcher);
    }

    @Override
    public SelectorCombinatorMatcher findSelectorCombinatorMatcher(String op) {
        return this.selectorCombinators.get(op);
    }

    @Override
    public String[] getSupportedSelectorCombinators() {
        return this.selectorCombinators.keySet().toArray(new String[this.selectorCombinators.size()]);
    }

    /**
     * @return the parent element node of <code>node</code> or <code>null</code> if <code>node</code> is the root
     *         element
     */
    public abstract T parent(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the child nodes of <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    public abstract List<? extends T> children(T node);

    /**
     * @return <code>true</code> of <code>node</code> is an element
     */
    public abstract boolean isElement(T node);

    /**
     * @return <code>true</code> of <code>node</code> is a text node
     */
    public abstract boolean isText(T node);

    /**
     * Must be invoked on text nodes only.
     *
     * @return the text contents of given text <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-text node
     * @see #isText(Object)
     */
    public abstract String text(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the local name for the given element <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    public abstract String name(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the namespace uri for the given element <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    public abstract String namespaceUri(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the attribute value for the attribute <code>name</code> having the namespace uri <code>uri</code> of the
     *         given element <code>node</code> or <code>null</code> if there is no such attribute
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    public abstract String attribute(T node, String uri, String name);

}
