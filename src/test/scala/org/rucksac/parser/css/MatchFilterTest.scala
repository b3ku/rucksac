package org.rucksac.parser.css

import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import org.junit.Test
import org.junit.Assert._
import org.rucksac.matcher.NodeMatcherRegistry

/**
 * Tests for the filter approach (which is forced by using the :eq() resp. :gt() pseudo functions)
 *
 * @author Oliver Becker
 * @since 02.10.12
 */
class MatchFilterTest {

    NodeMatcherRegistry.all()

    val documentAsText = "" +
        "<html>" +
        "  <body>" +
        "    <ul class='nav nav1'>" +
        "      <li id='11'>List 1, item 1</li>" +
        "      <li id='12'>List 1, item 2</li>" +
        "      <li id='13'>List 1, item 3</li>" +
        "    </ul>" +
        "    <ul class='nav nav2'>" +
        "      <li id='21'>List 2, item 1</li>" +
        "      <li id='22'>List 2, item 2</li>" +
        "      <li id='23'>List 2, item 3</li>" +
        "    </ul>" +
        "  </body>" +
        "</html>"

    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
        new ByteArrayInputStream(documentAsText.getBytes("UTF-8")));

    @Test
    def testEq1() {
        val result = $("ul.nav li:eq(1)", document)
        assertEquals(1, result.size)
        assertEquals("12", result(0).attribute("id"))
    }

    @Test
    def testEq2() {
        val result = $("ul.nav li:eq(5)", document)
        assertEquals(1, result.size)
        assertEquals("23", result(0).attribute("id"))
    }

    @Test
    def testEq3() {
        val result = $("ul.nav:eq(1) li:eq(2)", document)
        assertEquals(1, result.size)
        assertEquals("23", result(0).attribute("id"))
    }

    @Test
    def testEq4() {
        val result = $("ul.nav2 li:eq(2)", document)
        assertEquals(1, result.size)
        assertEquals("23", result(0).attribute("id"))
    }

    @Test
    def testEq5() {
        val result = $("body:eq(0) > ul.nav2 li:eq(2)", document)
        assertEquals(1, result.size)
        assertEquals("23", result(0).attribute("id"))
    }

    @Test
    def testEq6() {
        val result = $("ul.nav li:eq(0) + li", document)
        assertEquals(1, result.size)
        assertEquals("12", result(0).attribute("id"))
    }

    @Test
    def testEq7() {
        val result = $("ul.nav li + li:eq(0)", document)
        assertEquals(1, result.size)
        assertEquals("12", result(0).attribute("id"))
    }

    @Test
    def testEq8() {
        val result = $("ul.nav li:eq(1) + li:eq(0)", document)
        assertEquals(1, result.size)
        assertEquals("13", result(0).attribute("id"))
    }

    @Test
    def testEq9() {
        val result = $("ul.nav li:eq(1) ~ li:eq(0)", document)
        assertEquals(1, result.size)
        assertEquals("13", result(0).attribute("id"))
    }

    @Test
    def testEq10() {
        val result = $("ul.nav li:gt(-1) ~ li:gt(-1)", document)
        assertEquals(4, result.size)
        assertEquals("12", result(0).attribute("id"))
        assertEquals("13", result(1).attribute("id"))
        assertEquals("22", result(2).attribute("id"))
        assertEquals("23", result(3).attribute("id"))
    }

}
