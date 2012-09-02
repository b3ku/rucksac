package org.rucksac.parser.css

import org.w3c.dom.{Attr, Document}
import org.junit.{Ignore, Test, Before}
import javax.xml.parsers.DocumentBuilderFactory
import org.rucksac.Node

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
        document = DocumentBuilderFactory.newInstance.newDocumentBuilder.newDocument
        val root = createElement("root", "doc", null, null);
        (0 to 400) foreach {
            i =>
                val child = createElement("first", null, "eins uno un one", createAttribute("name", "bim"));
                root.appendChild(child);
                (0 to 1000) foreach {
                    j =>
                        child.appendChild(
                            createElement("second", null, "zwei dos deux two", createAttribute("name", "bam")));
                        child.appendChild(
                            createElement("third", null, "drei tres trois three", createAttribute("name", "bum")));
                }
        }
        document.appendChild(root);
    }

    @Test
    def testMatch() {
        new Query(Node(document, None)).findAll("root#doc > first.one.uno[name='bim'] *[name]")
    }

}
