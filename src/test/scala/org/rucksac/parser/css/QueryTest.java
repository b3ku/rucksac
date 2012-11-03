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

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rucksac.ParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Andreas Kuhrwahl
 * @since 10.08.12
 */
public class QueryTest {

    private Node document;

    private Iterator<Node> findAll(String query) {
        return Query4J.queryDom(query, Collections.singletonList(this.document)).iterator();
    }

    @Before
    public void setup() throws Exception {
        String documentAsText = ""
                + "<foo id='myFoo'>"
                + "  <bar id='b1' class='baz bum' name='bam' />"
                + "  <bar id='b2' class='last' name='bim bam'>Hello World</bar>"
                +
                "  <baz id='first' class='fazHolder' name='bim-bam-bum' disabled='disabled' checked='checked' dummy=''>"
                + "    <faz lang='en-us' />"
                + "  </baz>"
                + "  <wombat id='only' />"
                + "  <baz id='second' />"
                + "  <baz id='third' />"
                + "</foo>";

        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new ByteArrayInputStream(documentAsText.getBytes("UTF-8")));
    }

    private void assertNext(String expectedName, Iterator<Node> result) {
        assertNext(expectedName, null, result);
    }

    private void assertNext(String expectedName, String expectedId, Iterator<Node> result) {
        assertTrue(result.hasNext());
        Node node = result.next();
        assertTrue(node instanceof Element);
        Element e = (Element) node;
        Assert.assertEquals(expectedName, e.getTagName());
        if (expectedId != null) {
            Assert.assertEquals(expectedId, e.getAttribute("id"));
        }
    }

    @Test
    public void testAnyElement() {
        Iterator<Node> result = findAll("*");
        assertNext("foo", result);
        assertNext("bar", result);
        assertNext("bar", result);
        assertNext("baz", result);
        assertNext("faz", result);
        assertNext("wombat", result);
        assertNext("baz", result);
        assertNext("baz", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testSelectorList() {
        Iterator<Node> result = findAll("bar, *, foo");
        assertNext("foo", result);
        assertNext("bar", result);
        assertNext("bar", result);
        assertNext("baz", result);
        assertNext("faz", result);
        assertNext("wombat", result);
        assertNext("baz", result);
        assertNext("baz", result);
        assertFalse(result.hasNext());

        result = findAll("wombat, faz");
        assertNext("faz", result);
        assertNext("wombat", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementOfType() {
        Iterator<Node> result = findAll("bar");
        assertNext("bar", result);
        assertNext("bar", result);
        assertFalse(result.hasNext());

        result = findAll("foo");
        assertNext("foo", result);
        assertFalse(result.hasNext());

        result = findAll("foo, bar");
        assertNext("foo", result);
        assertNext("bar", result);
        assertNext("bar", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithAttribute() {
        Iterator<Node> result = findAll("bar[name]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("*[name]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[dummy]");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[dummy='']");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[none='']");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithAttributeValue() {
        Iterator<Node> result = findAll("bar[name=bam]");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIncludesOneAttributeValue() {
        Iterator<Node> result = findAll("[name~=bam]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("[name~=bim]");
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("[name~=bi]");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementStartsWithAttributeValue() {
        Iterator<Node> result = findAll("[name^=bim]");
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[name^=b]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementEndsWithAttributeValue() {
        Iterator<Node> result = findAll("[name$=bam]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("[name$=m]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementContainsAttributeValue() {
        Iterator<Node> result = findAll("[name*=bi]");
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[name*=bam]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[name*=am]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementStartsWithHyphenatedAttributeValue() {
        Iterator<Node> result = findAll("[name|=bim]");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("[name|=bum]");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsRoot() {
        Iterator<Node> result = findAll(":root");
        assertNext("foo", result);
        assertFalse(result.hasNext());

        result = findAll("bar:root");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsNthChild() {
        Iterator<Node> result = findAll("#myFoo > :nth-child(3)");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

//        result = findAll("#myFoo > :nth-child('odd')");
//        assertNext("bar", "b1", result);
//        assertNext("baz", "first", result);
//        assertNext("baz", "second", result);
//        assertFalse(result.hasNext());
//
//        result = findAll("#myFoo > :nth-child(2n)");
//        assertNext("bar", "b2", result);
//        assertNext("wombat", result);
//        assertNext("baz", "third", result);
//        assertFalse(result.hasNext());
//
//        result = findAll("#myFoo > :nth-child(-n+2)");
//        assertNext("bar", "b1", result);
//        assertNext("bar", "b2", result);
//        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsNthLastChild() {
        Iterator<Node> result = findAll("#myFoo > :nth-last-child(3)");
        assertNext("wombat", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > :nth-last-child('odd')");
        assertNext("bar", "b2", result);
        assertNext("wombat", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > :nth-last-child(2n)");
        assertNext("bar", result);
        assertNext("baz", result);
        assertNext("baz", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > :nth-last-child(-n+2)");
        assertNext("baz", result);
        assertNext("baz", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsNthOfType() {
        Iterator<Node> result = findAll("baz:nth-of-type(2)");
        assertNext("baz", "second", result);
        assertFalse(result.hasNext());

        result = findAll("baz:nth-of-type(odd)");
        assertNext("baz", "first", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());

        result = findAll("wombat:nth-of-type(2)");
        assertFalse(result.hasNext());

        result = findAll("*:nth-of-type(2n)");
        assertNext("bar", "b2", result);
        assertNext("baz", "second", result);
        assertFalse(result.hasNext());

        result = findAll("foo > *:nth-of-type(-n+2  )");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertNext("wombat", result);
        assertNext("baz", "second", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsNthLastOfType() {
        Iterator<Node> result = findAll("baz:nth-last-of-type(  2 )");
        assertNext("baz", "second", result);
        assertFalse(result.hasNext());

        result = findAll("baz:nth-last-of-type(odd)");
        assertNext("baz", "first", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());

        result = findAll("wombat:nth-last-of-type(2)");
        assertFalse(result.hasNext());

        result = findAll("*:nth-last-of-type(2n)");
        assertNext("bar", "b1", result);
        assertNext("baz", "second", result);
        assertFalse(result.hasNext());

        result = findAll("foo > *:nth-last-of-type(-n+2)");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("wombat", result);
        assertNext("baz", "second", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsFirstChild() {
        Iterator<Node> result = findAll("bar:first-child");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());

        result = findAll("bar.baz:first-child");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());

        result = findAll(":first-child");
        assertNext("foo", result);
        assertNext("bar", "b1", result);
        assertNext("faz", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsLastChild() {
        Iterator<Node> result = findAll("bar:last-child");
        assertFalse(result.hasNext());

        result = findAll("baz:last-child");
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsFirstOfType() {
        Iterator<Node> result = findAll("#myFoo > :first-of-type");
        assertNext("bar", "b1", result);
        assertNext("baz", "first", result);
        assertNext("wombat", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > bar:first-of-type");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsLastOfType() {
        Iterator<Node> result = findAll("#myFoo > :last-of-type");
        assertNext("bar", "b2", result);
        assertNext("wombat", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > bar:last-of-type");
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsOnlyChild() {
        Iterator<Node> result = findAll("faz:only-child");
        assertNext("faz", result);
        assertFalse(result.hasNext());

        result = findAll("* > bar:only-child");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsOnlyOfType() {
        Iterator<Node> result = findAll("bar:only-of-type");
        assertFalse(result.hasNext());

        result = findAll("#myFoo > wombat:only-of-type");
        assertNext("wombat", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithNoChildren() {
        Iterator<Node> result = findAll(":empty");
        assertNext("bar", "b1", result);
        assertNext("faz", result);
        assertNext("wombat", result);
        assertNext("baz", "second", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());

        result = findAll(".fazHolder > faz:empty");
        assertNext("faz", result);
        assertFalse(result.hasNext());

        result = findAll(".fazHolder > :empty");
        assertNext("faz", result);
        assertFalse(result.hasNext());
    }

    @Test(expected = ParseException.class)
    public void testElementIsHyperLink() {
        findAll(":link");
    }

    @Test(expected = ParseException.class)
    public void testElementIsVisitedHyperLink() {
        findAll(":visited");
    }

    @Test(expected = ParseException.class)
    public void testElementIsActivated() {
        findAll(":active");
    }

    @Test(expected = ParseException.class)
    public void testElementIsHovered() {
        findAll(":hover");
    }

    @Test(expected = ParseException.class)
    public void testElementIsFocused() {
        findAll(":focus");
    }

    @Test(expected = ParseException.class)
    public void testElementIsTarget() {
        findAll(":target");
    }

    @Test
    public void testElementIsInLanguage() {
        Iterator<Node> result = findAll(":lang(en)");
        assertNext("faz", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsEnabledOrDisabled() {
        Iterator<Node> result = findAll("#myFoo > :disabled");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > :enabled");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertNext("wombat", result);
        assertNext("baz", "second", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsChecked() {
        Iterator<Node> result = findAll("#myFoo > :checked");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

    @Test(expected = ParseException.class)
    public void testElementIsIndeterminate() {
        findAll(":indeterminate");
    }

    @Test
    public void testElementContainsText() {
        Iterator<Node> result = findAll(":contains('Hello')");
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll(":contains('World')");
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll(":contains('World!')");
        assertFalse(result.hasNext());
    }

    @Test(expected = ParseException.class)
    public void testFirstFormattedLineOfElement() {
        findAll("::first-line");
    }

    @Test(expected = ParseException.class)
    public void testFirstFormattedLetterOfElement() {
        findAll("::first-letter");
    }

    @Test(expected = ParseException.class)
    public void testCurrentSelectionOfElement() {
        findAll("::selection");
    }

    @Test(expected = ParseException.class)
    public void testGeneratedContentBeforeOfElement() {
        findAll("::before");
    }

    @Test(expected = ParseException.class)
    public void testGeneratedContentAfterOfElement() {
        findAll("::after");
    }

    @Test
    public void testElementWithClass() {
        Iterator<Node> result = findAll(".baz");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());

        result = findAll("bar.baz.bum[name]");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());

        result = findAll(".bam");
        assertFalse(result.hasNext());

        result = findAll("bar.baz");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());

        result = findAll("bar.bum");
        assertNext("bar", "b1", result);
        assertFalse(result.hasNext());

        result = findAll("foo.baz");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementWithId() {
        Iterator<Node> result = findAll("#myFoo");
        assertNext("foo", result);
        assertFalse(result.hasNext());

        result = findAll("foo#myFoo");
        assertNext("foo", result);
        assertFalse(result.hasNext());

        result = findAll("bar#myFoo");
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementNegation() {
        Iterator<Node> result = findAll("#myFoo > *[name*=bam]:not(baz)");
        assertNext("bar", result);
        assertNext("bar", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > *[name*=bam]:not([name~=bam])");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > *[name*=bam]:not(:enabled)");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsDescendant() {
        Iterator<Node> result = findAll("#myFoo [name~=bam]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo faz");
        assertNext("faz", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo .fazHolder[name] faz");
        assertNext("faz", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsChild() {
        Iterator<Node> result = findAll("#myFoo > [name~=bam]");
        assertNext("bar", "b1", result);
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("#myFoo > faz");
        assertFalse(result.hasNext());

        result = findAll("#myFoo > .fazHolder[name] > faz");
        assertNext("faz", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsDirectAdjacent() {
        Iterator<Node> result = findAll("#myFoo + bar");
        assertFalse(result.hasNext());

        result = findAll("bar.last + baz");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("bar.last + .fazHolder");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());

        result = findAll("bar.bum + [name~=bam]");
        assertNext("bar", "b2", result);
        assertFalse(result.hasNext());

        result = findAll("baz + baz");
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());
    }

    @Test
    public void testElementIsPreceded() {
        Iterator<Node> result = findAll("#myFoo ~ bar");
        assertFalse(result.hasNext());

        result = findAll("bar.baz ~ baz");
        assertNext("baz", "first", result);
        assertNext("baz", "second", result);
        assertNext("baz", "third", result);
        assertFalse(result.hasNext());

        result = findAll("bar.bum ~ [name*=bam]");
        assertNext("bar", "b2", result);
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

    @Test(expected = ParseException.class)
    public void testPseudoFunctionNotSupportedException() {
        findAll("::foo(bar)");
    }

    @Test(expected = ParseException.class)
    public void testSelectorCombinatorNotSupportedException() {
        findAll("foo < bar");
    }

    @Test(expected = ParseException.class)
    public void testPseudoClassNotSupportedException() {
        findAll(":foo");
    }

    @Test(expected = ParseException.class)
    public void testAttributeOperationNotSupportedException() {
        findAll("[foo#=bar]");
    }

    @Test
    @Ignore
    public void testEq() throws Exception {
        Iterator<Node> result = findAll("baz:eq(0)");
        assertNext("baz", "first", result);
        assertFalse(result.hasNext());
    }

}
