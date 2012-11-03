package org.rucksac.parser.matchers

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.rucksac.ParseException
import scala.xml.Node
import org.rucksac.parser.css.$._

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */
@Ignore
class jQueryMatchersTest {

    val xml: Node =
        <root class="oink">
            <button class="button"/>
            <input type="button" class="button"/>
        </root>

    @Test
    def testButton() {
        val result = List(xml).findAll(":button")
        assertEquals(2, result.size)
        assertEquals("button", (result(0) \ "@class").text)
        assertEquals("button", (result(1) \ "@class").text)
    }

    @Test
    def testNe() {
        var result = List(xml).findAll("[class=oink]")
        assertEquals(1, result.size)
        assertEquals("root", result(0).label)

        result = List(xml).findAll("[class!=oink]")
        assertEquals(2, result.size)
        assertEquals("button", (result(0) \ "@class").text)
        assertEquals("button", (result(1) \ "@class").text)

        result = List(xml).findAll("[class!='']")
        assertEquals(3, result.size)
        assertEquals("root", result(0).label)
        assertEquals("button", (result(1) \ "@class").text)
        assertEquals("button", (result(2) \ "@class").text)
    }

    @Test
    def testEq() {
        var result = List(xml).findAll(":eq(0)")
        assertEquals(1, result.size)
        assertEquals("root", result(0).label)

        result = List(xml).findAll(":eq(1)")
        assertEquals(1, result.size)
        assertEquals("button", result(0).label)

        result = List(xml).findAll(":eq(2)")
        assertEquals(1, result.size)
        assertEquals("input", result(0).label)

        result = List(xml).findAll(":eq(-1)")
        assertEquals(0, result.size)

        result = List(xml).findAll(":eq(5)")
        assertEquals(0, result.size)
    }

    @Test(expected = classOf[ParseException])
    def testEqFail() {
        List(xml).findAll(":eq(foo)")
    }

    @Test
    def testGt() {
        var result = List(xml).findAll(":gt(0)")
        assertEquals(2, result.size)
        assertEquals("button", result(0).label)
        assertEquals("input", result(1).label)

        result = List(xml).findAll("* > :gt(0)")
        assertEquals(1, result.size)
        assertEquals("input", result(0).label)
    }

    @Test
    def testLt() {
        var result = List(xml).findAll(":lt(2)")
        assertEquals(2, result.size)
        assertEquals("root", result(0).label)
        assertEquals("button", result(1).label)

        result = List(xml).findAll("* > :lt(1)")
        assertEquals(1, result.size)
        assertEquals("button", result(0).label)
    }

}
