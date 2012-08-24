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
 * @author Andreas Kuhrwahl
 * @since 12.08.12
 */
public abstract class NodeBrowser<T> implements NodeMatcherRegistry {

    private final Map<String, PseudoClassMatcher> pseudoClasses = new ConcurrentHashMap<String, PseudoClassMatcher>();

    public NodeBrowser() {
        init();
    }

    @Override
    public void registerPseudoClassMatcher(String pattern, PseudoClassMatcher pc) {
        this.pseudoClasses.put(pattern, pc);
    }

    @Override
    public PseudoClassMatcher findPseudoClassMatcher(String name) {
        //TODO patternmatcher
        PseudoClassMatcher pc = this.pseudoClasses.get(name);
        if (pc == null) {
            throw new PseudoClassNotSupportedException(name);
        }
        return pc;
    }

    protected final void applyNodeMatcherRegistrar(NodeMatcherRegistrar registrar) {
        registrar.registerNodeMatchers(this);
    }

    protected void init() {
    }

    public abstract T document(T node);

    public abstract T parent(T node);

    public abstract List<? extends T> children(T node);

    public abstract boolean isElement(T node);

    public abstract boolean isText(T node);

    public abstract String text(T node);

    public abstract String namespaceUri(T node);

    public abstract String name(T node);

    public abstract String attribute(T node, String uri, String name);

}
