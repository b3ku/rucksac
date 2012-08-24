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

/**
 * Interface for traversing a hierarchical node tree (having nodes of type <code>T</code>) that represents a HTML or XML
 * data structure.
 *
 * @author Andreas Kuhrwahl
 * @since 12.08.12
 */
public interface NodeBrowser<T> {

    /**
     * @return the parent element node of <code>node</code> or <code>null</code> if <code>node</code> is the root
     *         element
     */
    T parent(T node);

    /**
     * @return the child nodes of <code>node</code>
     */
    List<? extends T> children(T node);

    /**
     * @return <code>true</code> of <code>node</code> is an element
     */
    boolean isElement(T node);

    /**
     * @return <code>true</code> of <code>node</code> is a text node
     */
    boolean isText(T node);

    /**
     * Must be invoked on text nodes only.
     *
     * @return the text contents of given text <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-text node
     * @see #isText(Object)
     */
    String text(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the local name for the given element <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    String name(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the namespace uri for the given element <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    String namespaceUri(T node);

    /**
     * Must be invoked on element nodes only.
     *
     * @return the attribute value for the attribute <code>name</code> having the namespace uri <code>uri</code> of the
     *         given element <code>node</code> or <code>null</code> if there is no such attribute
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    String attribute(T node, String uri, String name);

}
