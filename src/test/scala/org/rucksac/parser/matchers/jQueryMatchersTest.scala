package org.rucksac.parser.matchers

import org.junit.Assert._
import org.junit.{Ignore, Test}
import org.rucksac.parser.css._
import org.rucksac.ParseException
import org.rucksac.matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @author Oliver Becker
 * @since 24.08.12
 */
class jQueryMatchersTest {

    NodeMatcherRegistry.all()

    val xml =
        <doc>
            <root class="oink">
                <button class="button1"/>
                <input type="button" class="input1"/>
                <button type="submit" class="button2"/>
                <input type="submit" class="input2"/>
            </root>
        </doc>

    //private def filter(query: String): Iterator[Query[Node]] = $(query, xml).iterator()

    @Test
    def testButton() {
        val result = $(":button", xml)
        assertEquals(3, result.size)
        assertEquals("button1", (result(0)() \ "@class").text)
        assertEquals("input1", (result(1)() \ "@class").text)
        assertEquals("button2", (result(2)() \ "@class").text)
    }

    @Test
    def testSubmit() {
        val result = $(":submit", xml)
        assertEquals(2, result.size)
        assertEquals("button2", (result(0)() \ "@class").text)
        assertEquals("input2", (result(1)() \ "@class").text)
    }

    @Test
    def testNe() {
        var result = $("[class=oink]", xml)
        assertEquals(1, result.size)
        assertEquals("root", result(0)().label)

        result = $("[class!=oink]", xml)
        assertEquals(4, result.size)
        assertEquals("button1", (result(0)() \ "@class").text)
        assertEquals("input1", (result(1)() \ "@class").text)
        assertEquals("button2", (result(2)() \ "@class").text)
        assertEquals("input2", (result(3)() \ "@class").text)

        result = $("[class!='']", xml)
        assertEquals(5, result.size)
        assertEquals("root", result(0)().label)
        assertEquals("button1", (result(1)() \ "@class").text)
        assertEquals("input1", (result(2)() \ "@class").text)
        assertEquals("button2", (result(3)() \ "@class").text)
        assertEquals("input2", (result(4)() \ "@class").text)
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
        assertEquals(4, result.size)
        assertEquals("button", result(0)().label)
        assertEquals("input", result(1)().label)
        assertEquals("button", result(2)().label)
        assertEquals("input", result(3)().label)

        result = $("* > :gt(0)", xml)
        assertEquals(3, result.size)
        assertEquals("input", result(0)().label)
        assertEquals("button", result(1)().label)
        assertEquals("input", result(2)().label)
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
