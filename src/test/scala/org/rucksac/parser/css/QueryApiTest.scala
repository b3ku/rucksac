package org.rucksac.parser.css

import org.w3c.dom
import org.w3c.dom.{Document, Element}
import org.junit.{Before, Test}
import org.junit.Assert._
import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import $._

/**
 * @author Andreas Kuhrwahl
 * @since 03.09.12
 */
class QueryApiTest {

    private var document: Document = null

    @Before
    def setup() {
        var documentAsText = "" +
            "<foo id='myFoo'>" +
            "  <bar id='b1' class='baz bum' name='bam' />" +
            "  <bar id='b2' class='last' name='bim bam'>Hello World</bar>" +
            "  <baz id='first' class='fazHolder' name='bim-bam-bum' disabled='disabled' checked='checked' dummy=''>" +
            "    <faz lang='en-us' />" +
            "  </baz>" +
            "  <wombat id='only' />" +
            "  <baz id='second' />" +
            "  <baz id='third' />" +
            "</foo>"
        document = DocumentBuilderFactory.newInstance.newDocumentBuilder.parse(
            new ByteArrayInputStream(documentAsText.getBytes("UTF-8")))
    }

    @Test
    def testApi() {
        val nodes = List[dom.Node](document).findAll("#myFoo > baz").filter("#first.fazHolder").findAll("faz")
        val attr = nodes(0) match {
            case e: Element => e.getAttribute("lang")
        }
        assertEquals("en-us", attr)
    }

    @Test(expected = classOf[ClassCastException])
    def testCorrectType() {
        List(document).findAll("bar").apply(0).createElement("egon")
    }

}
