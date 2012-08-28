package org.rucksac.parser.matchers

import org.junit.Assert._
import org.junit.{After, Before, Test}
import org.rucksac.parser.XmlNodeBrowser
import org.rucksac.parser.css._
import org.rucksac.{ParseException, NodeBrowser}
import scala.xml.Node

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */
class jQueryMatchersTest {

    val xml =
        <root class="oink">
            <button class="button"/>
            <input type="button" class="button"/>
        </root>

    private def filter(query: String): java.util.Iterator[Node] = new Query[Node](query).filter(xml).iterator()

    @Before
    def setup() {
        sys.props.put(classOf[NodeBrowser[XmlNodeBrowser]].getName, classOf[XmlNodeBrowser].getName)
    }

    @After
    def tearDown() {
        sys.props.remove(classOf[NodeBrowser[XmlNodeBrowser]].getName)
    }

    @Test
    def testButton() {
        val result = filter(":button")
        assertEquals("button", (result.next() \ "@class").text)
        assertEquals("button", (result.next() \ "@class").text)
        assertFalse(result.hasNext)
    }

    @Test
    def testNe() {
        var result = filter("[class=oink]")
        assertEquals("root", result.next().label)
        assertFalse(result.hasNext)

        result = filter("[class!=oink]")
        assertEquals("button", (result.next() \ "@class").text)
        assertEquals("button", (result.next() \ "@class").text)
        assertFalse(result.hasNext)

        result = filter("[class!='']")
        assertEquals("root", result.next().label)
        assertEquals("button", (result.next() \ "@class").text)
        assertEquals("button", (result.next() \ "@class").text)
        assertFalse(result.hasNext)
    }

    @Test
    def testEq() {
        var result = filter(":eq(0)")
        assertEquals("root", result.next().label)
        assertFalse(result.hasNext)

        result = filter(":eq(1)")
        assertEquals("button", result.next().label)
        assertFalse(result.hasNext)

        result = filter(":eq(2)")
        assertEquals("input", result.next().label)
        assertFalse(result.hasNext)

        result = filter(":eq(-1)")
        assertFalse(result.hasNext)

        result = filter(":eq(5)")
        assertFalse(result.hasNext)
    }

    @Test(expected = classOf[ParseException])
    def testEqFail() {
        filter(":eq(foo)")
    }

    @Test
    def testGt() {
        var result = filter(":gt(0)")
        assertEquals("button", result.next().label)
        assertEquals("input", result.next().label)
        assertFalse(result.hasNext)

        // FIXME
//        result = filter("* > :gt(0)")
//        assertEquals("input", result.next().label)
//        assertFalse(result.hasNext)
    }

    @Test
    def testLt() {
        var result = filter(":lt(2)")
        assertEquals("root", result.next().label)
        assertEquals("button", result.next().label)
        assertFalse(result.hasNext)

        // FIXME
//        result = filter("* > :lt(1)")
//        assertEquals("input", result.next().label)
//        assertFalse(result.hasNext)
    }

}
