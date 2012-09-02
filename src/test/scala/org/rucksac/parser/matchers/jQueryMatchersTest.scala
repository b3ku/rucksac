package org.rucksac.parser.matchers

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.rucksac.parser.css._
import org.rucksac.ParseException
import org.rucksac.matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */
@Ignore
class jQueryMatchersTest {

    NodeMatcherRegistry.all()

    val xml =
        <root class="oink">
            <button class="button"/>
            <input type="button" class="button"/>
        </root>

    //private def filter(query: String): Iterator[Query[Node]] = $(query, xml).iterator()

    @Test
    def testButton() {
        val result = $(":button", xml)
        assertEquals(2, result.size)
        assertEquals("button", (result(0)() \ "@class").text)
        assertEquals("button", (result(1)() \ "@class").text)
    }

    @Test
    def testNe() {
        var result = $("[class=oink]", xml)
        assertEquals(1, result.size)
        assertEquals("root", result(0)().label)

        result = $("[class!=oink]", xml)
        assertEquals(2, result.size)
        assertEquals("button", (result(0)() \ "@class").text)
        assertEquals("button", (result(1)() \ "@class").text)

        result = $("[class!='']", xml)
        assertEquals(3, result.size)
        assertEquals("root", result(0)().label)
        assertEquals("button", (result(1)() \ "@class").text)
        assertEquals("button", (result(2)() \ "@class").text)
    }

    @Test
    def testEq() {
        var result = $(":eq(0)", xml)
        assertEquals(1, result.size)
        assertEquals("root", result(0)().label)

        result = $(":eq(1)", xml)
        assertEquals(1, result.size)
        assertEquals("button", result(0)().label)

        result = $(":eq(2)", xml)
        assertEquals(1, result.size)
        assertEquals("input", result(0)().label)

        result = $(":eq(-1)", xml)
        assertEquals(0, result.size)

        result = $(":eq(5)", xml)
        assertEquals(0, result.size)
    }

    @Test(expected = classOf[ParseException])
    def testEqFail() {
        $(":eq(foo)", xml)
    }

    @Test
    def testGt() {
        var result = $(":gt(0)", xml)
        assertEquals(2, result.size)
        assertEquals("button", result(0)().label)
        assertEquals("input", result(1)().label)

        result = $("* > :gt(0)", xml)
        assertEquals(1, result.size)
        assertEquals("input", result(0)().label)
    }

    @Test
    def testLt() {
        var result = $(":lt(2)", xml)
        assertEquals(2, result.size)
        assertEquals("root", result(0)().label)
        assertEquals("button", result(1)().label)

        result = $("* > :lt(1)", xml)
        assertEquals(1, result.size)
        assertEquals("button", result(0)().label)
    }

}
