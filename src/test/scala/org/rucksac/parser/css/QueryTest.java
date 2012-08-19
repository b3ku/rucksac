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
package org.rucksac.parser.css;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rucksac.AttributeOperationNotSupportedException;
import org.rucksac.PseudoClassNotSupportedException;
import org.rucksac.PseudoFunctionNotSupportedException;
import org.rucksac.SelectorCombinatorNotSupportedException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Andreas Kuhrwahl
 * @since 10.08.12
 */
//TODO Travis-CI!
public class QueryTest {

    private Document document;

    private Iterator<Node> $(String query) {
        return Query.<Node>$(query).filter(this.document).iterator();
    }

    private Element createElement(String name, String id, String styleClass, Attr attr) {
        Element result = this.document.createElement(name);
        if (id != null) {
            result.getAttributes().setNamedItem(createAttribute("id", id));
        }
        if (styleClass != null) {
            result.getAttributes().setNamedItem(createAttribute("class", styleClass));
        }
        if (attr != null) {
            result.getAttributes().setNamedItem(attr);
        }
        return result;

    }

    private Attr createAttribute(String name, String value) {
        Attr attribute = this.document.createAttribute(name);
        attribute.setValue(value);
        return attribute;
    }

    @Before
    public void setup() throws Exception {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = createElement("foo", "myFoo", null, null);
        root.appendChild(createElement("bar", null, "baz bum", createAttribute("name", "bam")));
        root.appendChild(createElement("bar", null, "last", createAttribute("name", "bim bam")));
        Element baz = createElement("baz", null, "fazHolder", createAttribute("name", "bim-bam-bum"));
        baz.appendChild(createElement("faz", null, null, null));
        root.appendChild(baz);
        this.document.appendChild(root);
    }

    void assertEquals(String expected, Node node) {
        assertTrue(node instanceof Element);
        Element e = (Element) node;
        Assert.assertEquals(expected, e.getTagName());
    }

    @Test
    public void testAnyElement() {
        Iterator<Node> result = $("*");
        assertEquals("foo", result.next());
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementOfType() {
        Iterator<Node> result = $("bar");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("foo");
        assertEquals("foo", result.next());
        assertFalse(result.hasNext());

        result = $("foo, bar");
        assertEquals("foo", result.next());
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithAttribute() {
        Iterator<Node> result = $("bar[name]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("*[name]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithAttributeValue() {
        Iterator<Node> result = $("bar[name=bam]");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIncludesOneAttributeValue() {
        Iterator<Node> result = $("[name~=bam]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("[name~=bim]");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("[name~=bi]");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementStartsWithAttributeValue() {
        Iterator<Node> result = $("[name^=bim]");
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("[name^=b]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementEndsWithAttributeValue() {
        Iterator<Node> result = $("[name$=bam]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("[name$=m]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementContainsAttributeValue() {
        Iterator<Node> result = $("[name*=bi]");
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("[name*=am]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementStartsWithHyphenatedAttributeValue() {
        Iterator<Node> result = $("[name|=bim]");
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("[name|=bum]");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsRoot() {
        Iterator<Node> result = $(":root");
        assertEquals("foo", result.next());
        assertFalse(result.hasNext());

        result = $("bar:root");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsNthChild() {
        fail();
    }

    @Test
    public void testElementIsNthLastChild() {
        fail();
    }

    @Test
    public void testElementIsNthOfType() {
        fail();
    }

    @Test
    public void testElementIsNthLastOfType() {
        fail();
    }

    @Test
    public void testElementIsFirstChild() {
        Iterator<Node> result = $("bar:first-child");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("bar.baz:first-child");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $(":first-child");
        assertEquals("foo", result.next());
        assertEquals("bar", result.next());
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsLastChild() {
        Iterator<Node> result = $("bar:last-child");
        assertFalse(result.hasNext());

        result = $("baz:last-child");
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsFirstOfType() {
        Iterator<Node> result = $("#myFoo > :first-of-type");
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("#myFoo > bar:first-of-type");
        Element element = (Element) result.next();
        Assert.assertEquals("bar", element.getTagName());
        Assert.assertEquals("bam", element.getAttribute("name"));
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsLastOfType() {
        Iterator<Node> result = $("#myFoo > :last-of-type");
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("#myFoo > bar:last-of-type");
        Element element = (Element) result.next();
        Assert.assertEquals("bar", element.getTagName());
        Assert.assertEquals("bim bam", element.getAttribute("name"));
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsOnlyChild() {
        Iterator<Node> result = $("faz:only-child");
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());

        result = $("* > bar:only-child");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsOnlyOfType() {
        Iterator<Node> result = $("bar:only-of-type");
        assertFalse(result.hasNext());

        result = $("#myFoo > baz:only-of-type");
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithNoChildren() {
        Iterator<Node> result = $(":empty");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());

        result = $(".fazHolder > faz:empty");
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());

        result = $(".fazHolder > :empty");
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsHyperLink() {
        $(":link");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsVisitedHyperLink() {
        $(":visited");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsActivated() {
        $(":active");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsHovered() {
        $(":hover");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsFocused() {
        $(":focus");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsTarget() {
        $(":target");
    }

    @Test
    public void testElementIsInLanguage() {
        fail();
    }

    @Test
    public void testElementIsEnabledOrDisabled() {
        fail();
    }

    @Test
    public void testElementIsChecked() {
        fail();
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testElementIsIndeterminate() {
        $(":indeterminate");
    }

    @Test
    public void testElementContainsText() {
        fail();
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testFirstFormattedLineOfElement() {
        $("::first-line");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testFirstFormattedLetterOfElement() {
        $("::first-letter");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testCurrentSelectionOfElement() {
        $("::selection");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testGeneratedContentBeforeOfElement() {
        $("::before");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testGeneratedContentAfterOfElement() {
        $("::after");
    }

    @Test
    public void testElementWithClass() {
        Iterator<Node> result = $(".baz");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("bar.baz.bum[name]");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $(".bam");
        assertFalse(result.hasNext());

        result = $("bar.baz");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("bar.bum");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("foo.baz");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithId() {
        Iterator<Node> result = $("#myFoo");
        assertEquals("foo", result.next());
        assertFalse(result.hasNext());

        result = $("foo#myFoo");
        assertEquals("foo", result.next());
        assertFalse(result.hasNext());

        result = $("bar#myFoo");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementNegation() {
        fail();
    }

    @Test
    public void testElementIsDescendant() {
        Iterator<Node> result = $("#myFoo [name~=bam]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("#myFoo faz");
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());

        result = $("#myFoo .fazHolder[name] faz");
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsChild() {
        Iterator<Node> result = $("#myFoo > [name~=bam]");
        assertEquals("bar", result.next());
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());

        result = $("#myFoo > faz");
        assertFalse(result.hasNext());

        result = $("#myFoo > .fazHolder[name] > faz");
        assertEquals("faz", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsDirectAdjacent() {
        Iterator<Node> result = $("#myFoo + bar");
        assertFalse(result.hasNext());

        result = $("bar.last + baz");
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("bar.last + .fazHolder");
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("bar.bum + [name~=bam]");
        assertEquals("bar", result.next());
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsPreceded() {
        Iterator<Node> result = $("#myFoo ~ bar");
        assertFalse(result.hasNext());

        result = $("bar.baz ~ baz");
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());

        result = $("bar.bum ~ [name*=bam]");
        assertEquals("bar", result.next());
        assertEquals("baz", result.next());
        assertFalse(result.hasNext());
    }

    @Test(expected = PseudoFunctionNotSupportedException.class)
    public void testPseudoFunctionNotSupportedException() {
        $("::foo(bar)");
    }

    @Test(expected = SelectorCombinatorNotSupportedException.class)
    @Ignore
    public void testSelectorCombinatorNotSupportedException() {
        $("foo < bar");
    }

    @Test(expected = PseudoClassNotSupportedException.class)
    public void testPseudoClassNotSupportedException() {
        $(":foo");
    }

    @Test(expected = AttributeOperationNotSupportedException.class)
    @Ignore
    public void testAttributeOperationNotSupportedException() {
        $("[foo!=bar]");
    }

}
