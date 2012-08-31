package org.rucksac.parser.css;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Oliver Becker
 * @since 31.08.12
 */
public class PerformanceTest {

    private Document document;


    private Element createElement(String name, String id, String styleClass, Attr attr) {
        Element result = this.document.createElement(name);
        if (id != null) {
            result.getAttributes().setNamedItem(createAttribute("id", id));
        }
        if (styleClass != null) {
            result.getAttributes().setNamedItem(createAttribute("class", styleClass));
        }
        if (attr != null) {
            result.getAttributes().setNamedItem(attr);
        }
        return result;
    }


    private Attr createAttribute(String name, String value) {
        Attr attribute = this.document.createAttribute(name);
        attribute.setValue(value);
        return attribute;
    }

    @Before
    public void setup() throws ParserConfigurationException {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = createElement("root", "doc", null, null);
        for (int i = 0; i < 400; i++) {
            Element child = createElement("first", null, "eins uno un one", createAttribute("name", "bim"));
            root.appendChild(child);
            for (int j = 0; j < 1000; j++) {
                child.appendChild(createElement("second", null, "zwei dos deux two", createAttribute("name", "bam")));
                child.appendChild(createElement("third", null, "drei tres trois three", createAttribute("name", "bum")));
            }
        }
        this.document.appendChild(root);
    }

    @Test
    public void testMatch() throws Exception {
        new Query<Node>("root#doc > first.one.uno[name='bim'] *[name]").filter(this.document.getDocumentElement());
    }

}
