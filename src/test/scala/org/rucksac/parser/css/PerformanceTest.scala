package org.rucksac.parser.css

import org.w3c.dom.{Attr, Document}
import org.junit.{Ignore, Test, Before}
import org.junit.Assert._
import javax.xml.parsers.DocumentBuilderFactory
import org.rucksac.matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @since 01.09.12
 */
@Ignore
class PerformanceTest {

    private var document: Document = null

    private def createElement(name: String, id: String, styleClass: String, attr: Attr) = {
        val result = document.createElement(name)
        if (id != null) {
            result.getAttributes.setNamedItem(createAttribute("id", id))
        }
        if (styleClass != null) {
            result.getAttributes.setNamedItem(createAttribute("class", styleClass))
        }
        if (attr != null) {
            result.getAttributes.setNamedItem(attr)
        }
        result
    }

    private def createAttribute(name: String, value: String) = {
        val attr = document.createAttribute(name)
        attr.setValue(value)
        attr
    }

    val loop1 = 200
    val loop2 = 100

    @Before
    def setup() {
        NodeMatcherRegistry.all()

        document = DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument
        val root = createElement("root", "doc", null, null)
        (0 until loop1) foreach {
            i =>
                val child = createElement("first", null, "eins uno un one", createAttribute("name", "bim"))
                root.appendChild(child)
                (0 until loop2) foreach {
                    j =>
                        child.appendChild(
                            createElement("second", null, "zwei dos deux two", createAttribute("name", "bam")))
                        child.appendChild(
                            createElement("third", null, "drei tres trois three", createAttribute("name", "bum")))
                }
        }
        document.appendChild(root)
    }

    @Test
    def testPredicate() {
        executeTest("root#doc > first.one.uno[name='bim'] *[name]")
    }

    @Test
    def testFilter() {
        executeTest("root#doc:eq(0) > first.one.uno[name='bim']:gt(-1) *[name]:gt(-1)")
    }

    private def executeTest(expression: String) {
        System.gc()
        val start = System.currentTimeMillis()
        val result = $(expression, document)
        println("Query took " + (System.currentTimeMillis() - start)/1000.0 + " seconds")
        assertEquals(loop1 * loop2 * 2, result.size)
    }

}
