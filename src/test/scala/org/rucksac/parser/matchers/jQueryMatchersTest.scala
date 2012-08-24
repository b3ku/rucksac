package org.rucksac.parser.matchers

import org.junit.Test
import javax.xml.parsers.DocumentBuilderFactory
import org.rucksac.parser.css._
import org.junit.Assert._
import org.w3c.dom.Element

/**
 * @author Andreas Kuhrwahl
 * @since 24.08.12
 */
class jQueryMatchersTest {

    val document = DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument;
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
        val result = $(":button").filter(root).iterator()
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)
    }

    @Test
    def testNe() {
        var result = $("[class=oink]").filter(root).iterator()
        assertEquals("root", result.next().asInstanceOf[Element].getTagName)
        assertFalse(result.hasNext)

        result = $("[class!=oink]").filter(root).iterator()
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)

        result = $("[class!='']").filter(root).iterator()
        assertEquals("root", result.next().asInstanceOf[Element].getTagName)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)
    }

}
