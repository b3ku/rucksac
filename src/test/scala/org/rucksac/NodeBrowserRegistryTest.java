package org.rucksac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rucksac.parser.css.Query4J;

/**
 * @author Oliver Becker
 * @since 04.11.12
 */
public class NodeBrowserRegistryTest {

    interface TestNode {
        public String getName();
        public List<TestNode> getChildren();
    }

    class TestElement implements TestNode {

        private String name;
        private String id;
        private List<TestNode> children = new ArrayList<TestNode>();

        public TestElement(String name) {
            this.name = name;
        }

        public TestElement(String name, String id) {
            this(name);
            this.id = id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<TestNode> getChildren() {
            return this.children;
        }
    }

    class TestDocument implements TestNode {
        public TestElement root;

        @Override
        public String getName() {
            return null;
        }

        @Override
        public List<TestNode> getChildren() {
            return Collections.<TestNode>singletonList(root);
        }
    }

    class TestNodeBrowser extends NodeBrowser4J<TestNode> {

        @Override
        public List<TestNode> getChildren(TestNode node) {
            return node.getChildren();
        }

        @Override
        public boolean isDocument(TestNode node) {
            return false;
        }

        @Override
        public boolean isElement(TestNode node) {
            return node instanceof TestElement;
        }

        @Override
        public boolean isText(TestNode node) {
            return false;
        }

        @Override
        public String text(TestNode node) {
            return "";
        }

        @Override
        public String name(TestNode node) {
            return node.getName();
        }

        @Override
        public String namespaceUri(TestNode node) {
            return null;
        }

        @Override
        public String attribute(TestNode node, String uri, String name) {
            return isElement(node) && "id".equals(name) ? ((TestElement)node).id : null;
        }
    }

    private TestDocument doc;

    @Before
    public void setUp() throws Exception {
        this.doc = new TestDocument();
        this.doc.root = new TestElement("html");
        this.doc.root.getChildren().add(new TestElement("head"));
        TestElement body = new TestElement("body");
        TestElement content = new TestElement("div", "content");
        content.getChildren().add(new TestElement("p", "first"));
        content.getChildren().add(new TestElement("p", "second"));
        content.getChildren().add(new TestElement("p", "third"));
        body.getChildren().add(content);
        TestElement footer = new TestElement("div", "footer");
        body.getChildren().add(footer);
        this.doc.root.getChildren().add(body);

        NodeBrowserRegistry.register(TestNode.class, new TestNodeBrowser());
    }

    private Iterator<TestNode> query(String q) {
        return Query4J.$(q, (TestNode) this.doc).iterator();
    }

    private TestElement assertNextElement(Iterator<TestNode> result) {
        assertTrue(result.hasNext());
        TestNode next = result.next();
        assertTrue(next instanceof TestElement);
        return (TestElement) next;
    }

    @Test
    public void testQuery1() throws Exception {
        Iterator<TestNode> result = query("#content > p:last-child");
        TestElement el = assertNextElement(result);
        assertEquals("p", el.getName());
        assertEquals("third", el.id);
        assertFalse(result.hasNext());
    }

    @Test
    public void testQuery2() throws Exception {
        Iterator<TestNode> result = query("html #footer");
        TestElement el = assertNextElement(result);
        assertEquals("div", el.getName());
        assertFalse(result.hasNext());
    }

}
