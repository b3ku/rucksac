package org.rucksac.parser.css

import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import org.junit.{Ignore, Test}
import org.junit.Assert._
import org.rucksac.matcher.NodeMatcherRegistry

/**
 * Tests for the filter approach (which is forced by using positional pseudo classes resp. functions)
 *
 * @author Oliver Becker
 * @since 02.10.12
 */
class PositionalMatchFilterTest {

    NodeMatcherRegistry.all()

    val documentAsText = "" +
            "<html>" +
            "  <body>" +
            "    <ul class='nav nav1'>" +
            "      <li id='x11'>List 1, item 1</li>" +
            "      <li id='x12'>List 1, item 2</li>" +
            "      <li id='x13'>List 1, item 3</li>" +
            "    </ul>" +
            "    <ul class='nav nav2'>" +
            "      <li id='x21'>List 2, item 1</li>" +
            "      <li id='x22'>List 2, item 2</li>" +
            "      <li id='x23'>List 2, item 3</li>" +
            "    </ul>" +
            "  </body>" +
            "</html>"

    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
        new ByteArrayInputStream(documentAsText.getBytes("UTF-8")));

    @Test
    def testDescendents1() {
        val result = $("ul.nav li:eq(1)", document)
        assertEquals(1, result.size)
        assertEquals("x12", result(0).attribute("id"))
    }

    @Test
    def testDescendents2() {
        val result = $("ul.nav li:eq(5)", document)
        assertEquals(1, result.size)
        assertEquals("x23", result(0).attribute("id"))
    }

    @Test
    def testDescendents3() {
        val result = $("ul.nav:eq(1) li:eq(2)", document)
        assertEquals(1, result.size)
        assertEquals("x23", result(0).attribute("id"))
    }

    @Test
    def testDescendents4() {
        val result = $("ul.nav2 li:eq(2)", document)
        assertEquals(1, result.size)
        assertEquals("x23", result(0).attribute("id"))
    }

    @Test
    def testChildren() {
        val result = $("body:eq(0) > ul.nav2 li:eq(2)", document)
        assertEquals(1, result.size)
        assertEquals("x23", result(0).attribute("id"))
    }

    @Test
    def testAdjacentSiblings1() {
        val result = $("ul.nav li:eq(0) + li", document)
        assertEquals(1, result.size)
        assertEquals("x12", result(0).attribute("id"))
    }

    @Test
    def testAdjacentSiblings2() {
        val result = $("ul.nav li + li:eq(0)", document)
        assertEquals(1, result.size)
        assertEquals("x12", result(0).attribute("id"))
    }

    @Test
    def testAdjacentSiblings3() {
        val result = $("ul.nav li:eq(1) + li:eq(0)", document)
        assertEquals(1, result.size)
        assertEquals("x13", result(0).attribute("id"))
    }

    @Test
    def testGeneralSiblings1() {
        val result = $("ul.nav li:eq(1) ~ li:eq(0)", document)
        assertEquals(1, result.size)
        assertEquals("x13", result(0).attribute("id"))
    }

    @Test
    def testGeneralSiblings2() {
        val result = $("ul.nav li:gt(-1) ~ li:gt(-1)", document)
        assertEquals(4, result.size)
        assertEquals("x12", result(0).attribute("id"))
        assertEquals("x13", result(1).attribute("id"))
        assertEquals("x22", result(2).attribute("id"))
        assertEquals("x23", result(3).attribute("id"))
    }

    @Test
    @Ignore("FIXME")
    def testFilterWithChildSelector() {
        var result = $("li", document).filter("ul.nav1 > li:eq(1)")
        assertEquals(1, result.size)
        assertEquals("x12", result(0).attribute("id"))

        result = $("li", document).filter("ul.nav2 > li:eq(1)")
        assertEquals(1, result.size)
        assertEquals("x22", result(0).attribute("id"))
    }

    @Test
    def testFilterWithSelectorList() {
        var result = $("li", document).filter("#x21, #x13")
        assertEquals(2, result.size)
        assertEquals("x13", result(0).attribute("id"))
        assertEquals("x21", result(1).attribute("id"))
    }

    @Test
    def testEven() {
        var result = $("li:even", document)
        assertEquals(3, result.size)
        assertEquals("x11", result(0).attribute("id"))
        assertEquals("x13", result(1).attribute("id"))
        assertEquals("x22", result(2).attribute("id"))
    }

    @Test
    def testOdd() {
        var result = $("li:odd", document)
        assertEquals(3, result.size)
        assertEquals("x12", result(0).attribute("id"))
        assertEquals("x21", result(1).attribute("id"))
        assertEquals("x23", result(2).attribute("id"))
    }

    @Test
    def testFirst() {
        var result = $("li:first", document)
        assertEquals(1, result.size)
        assertEquals("x11", result(0).attribute("id"))
    }

    @Test
    def testLast() {
        var result = $("li:last", document)
        assertEquals(1, result.size)
        assertEquals("x23", result(0).attribute("id"))
    }

}
