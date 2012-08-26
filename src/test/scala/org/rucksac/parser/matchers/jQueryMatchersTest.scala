package org.rucksac.parser.matchers

import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory
import org.rucksac.parser.css._
import org.junit.Assert._

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */
class jQueryMatchersTest {

    val document = DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument
    val root     = document.createElement("root")
    root.setAttribute("class", "oink")
    document.appendChild(root)

    //Button
    var el = document.createElement("button")
    el.setAttribute("class", "button")
    root.appendChild(el)
    el = document.createElement("input")
    el.setAttribute("type", "button")
    el.setAttribute("class", "button")
    root.appendChild(el)

    @Test
    def testButton() {
        val result = Query(":button").filter(root).iterator()
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)
    }

    @Test
    def testNe() {
        var result = Query("[class=oink]").filter(root).iterator()
        assertEquals("root", result.next().getTagName)
        assertFalse(result.hasNext)

        result = Query("[class!=oink]").filter(root).iterator()
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)

        result = Query("[class!='']").filter(root).iterator()
        assertEquals("root", result.next().getTagName)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)
    }

    @Test
    def testEq() {
        var result = Query(":eq(1)").filter(root).iterator()
        assertEquals("root", result.next().getTagName)
        assertFalse(result.hasNext)

        result = Query(":eq(2)").filter(root).iterator()
        assertEquals("button", result.next().getTagName)
        assertFalse(result.hasNext)

        result = Query(":eq(3)").filter(root).iterator()
        assertEquals("input", result.next().getTagName)
        assertFalse(result.hasNext)

    }

}
