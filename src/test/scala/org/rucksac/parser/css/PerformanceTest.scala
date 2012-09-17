package org.rucksac.parser.css

import org.w3c.dom.{Attr, Document}
import org.junit.{Ignore, Test, Before}
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

    @Before
    def setup() {
        NodeMatcherRegistry.all()

        document = DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument
        val root = createElement("root", "doc", null, null)
        (0 to 400) foreach {
            i =>
                val child = createElement("first", null, "eins uno un one", createAttribute("name", "bim"))
                root.appendChild(child)
                (0 to 1000) foreach {
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
        val start = System.currentTimeMillis()
        $("root#doc > first.one.uno[name='bim'] *[name]", document)
        println("Query took " + (System.currentTimeMillis() - start)/1000.0 + " seconds")
    }

    @Test
    def testFilter() {
        val start = System.currentTimeMillis()
        $("root#doc:eq(0) > first.one.uno[name='bim'] *[name]", document)
        println("Query took " + (System.currentTimeMillis() - start)/1000.0 + " seconds")
    }

}
