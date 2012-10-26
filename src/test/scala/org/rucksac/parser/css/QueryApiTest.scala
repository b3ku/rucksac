package org.rucksac.parser.css

import org.w3c.dom.{Node, Element}
import org.junit.{Before, Test}
import org.junit.Assert._
import javax.xml.parsers.DocumentBuilderFactory
import java.io.ByteArrayInputStream
import org.rucksac.matcher.NodeMatcherRegistry
import org.rucksac.NodeBrowserRegistry
import collection.{SeqLike, mutable}
import collection.mutable.ArrayBuffer

/**
 * @author Andreas Kuhrwahl
 * @since 03.09.12
 */
class QueryApiTest {

    private var document: Node = null

    @Before
    def setup() {
        NodeMatcherRegistry.all()

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
        val nodes = $("#myFoo > baz", document).filter("#first.fazHolder:eq(0)").@@("faz")
        assertEquals(nodes()(0), nodes(0)())

        val attr = nodes()(0) match {
            case e: Element => e.getAttribute("lang")
        }
        assertEquals("en-us", attr)
    }

    @Test
    def testOperands() {
        val nodes1 = $("#myFoo", document) @@> "bar" @@+ "baz" @@ "faz"
        val nodes2 = $("#myFoo > bar + baz faz", document)
        assertEquals(nodes1, nodes2)
        assertEquals("faz", nodes1()(0).getNodeName)
    }

    @Test
    def testCorrectType() {
        val nodes = $("#myFoo > baz", document)
        val extNodes = nodes.filter("baz").tail
        assert(extNodes.isInstanceOf[Query[_]])
    }

    @Test
    def testExtensionType() {
        type NodeNode = org.rucksac.Node[Node]
        class MyQuery(seq: Seq[NodeNode]) extends Query[Node](seq) with SeqLike[NodeNode, MyQuery] {
            override def newBuilder: mutable.Builder[NodeNode, MyQuery] =
                new mutable.Builder[NodeNode, MyQuery] {
                    def emptyList() = new ArrayBuffer[NodeNode]()

                    var current = emptyList()

                    def +=(elem: NodeNode) = {
                        current += elem
                        this
                    }

                    def clear() { current = emptyList() }
                    def result() = new MyQuery(current)
                }
        }

        val nodes = new MyQuery(List(org.rucksac.Node(document, None, NodeBrowserRegistry(document)))).@@("#myFoo > baz")
        val extNodes = nodes.filter("baz").tail
        assert(extNodes.isInstanceOf[MyQuery])
    }

}
