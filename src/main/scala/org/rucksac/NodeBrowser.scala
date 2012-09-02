package org.rucksac

/**
 * Interface for traversing a hierarchical node tree (having nodes of type <code>T</code>) that represents a HTML or XML
 * data structure.
 *
 * @author Andreas Kuhrwahl
 * @since 12.08.12
 */
trait NodeBrowser[T] {

    /**
     * Get the children of an node.
     *
     * @return the child nodes of <code>node</code>, an empty list if there are no children
     */
    def children(node: T): Seq[T]

    /**
     * @return <code>true</code> if <code>node</code> is a document
     */
    def isDocument(node: T): Boolean

    /**
     * @return <code>true</code> if <code>node</code> is an element
     */
    def isElement(node: T): Boolean

    /**
     * @return <code>true</code> if <code>node</code> is a text node
     */
    def isText(node: T): Boolean

    /**
     * Get the text content of a text node. Must be invoked on text nodes only.
     *
     * @return the text content of given text <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-text node
     * @see #isText(Object)
     */
    def text(node: T): String

    /**
     * Get the local name of an element node. Must be invoked on element nodes only.
     *
     * @return the local name for the given element <code>node</code>
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    def name(node: T): String

    /**
     * Get the namespace URI of an node.
     *
     * @return the namespace uri for the given element <code>node</code>
     */
    def namespaceUri(node: T): String

    /**
     * Get an attribute volue. Must be invoked on element nodes only.
     *
     * @return the attribute value for the attribute <code>name</code> having the namespace uri <code>uri</code> of the
     *         given element <code>node</code> or <code>null</code> if there is no such attribute
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement(Object)
     */
    def attribute(node: T, uri: String, name: String): String

}

object NodeBrowser {

    val NODE_BROWSER         = getClass.getName + ".nodeBrowser"
    val DEFAULT_NODE_BROWSER = Option(classOf[DomNodeBrowser].getName)

    val nodeBrowserType = sys.props.get(NODE_BROWSER).orElse(DEFAULT_NODE_BROWSER).get
    val nodeBrowser     = Class.forName(nodeBrowserType).newInstance()

    def apply[T]() = nodeBrowser.asInstanceOf[NodeBrowser[T]]

}
