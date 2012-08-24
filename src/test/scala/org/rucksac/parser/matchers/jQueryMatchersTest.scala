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

    val document = DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument;
    val root     = document.createElement("root")
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
        val result = $(":button").filter(document).iterator()
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertEquals("button", result.next().getAttributes.getNamedItem("class").getNodeValue)
        assertFalse(result.hasNext)
    }

}
