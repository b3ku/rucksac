package org.rucksac

import collection._

/**
 * @author Andreas Kuhrwahl
 * @since 30.08.12
 */

abstract class Node {
    self =>

    protected type Orig

    def apply(): Orig

    def companion: NodeFactory[Orig]

    implicit protected def asNode(n: Orig): Node = companion.asNode(n)

    /**
     * Get the orginal parent of this node.
     *
     * @return the parent node or <code>null</code> if there's no parent or parent is document
     */
    val _parent: Orig

    /**
     * Get the original children of this node.
     *
     * @return the child nodes, an empty list if there are no children
     */
    val _children: Seq[Orig]

    def parent: Option[Node] = Option(_parent) match {
        case Some(p) => Option(p)
        case _ => None
    }

    def children: Seq[Node] = {
        val children = _children
        new immutable.IndexedSeq[Node] {
            def length = children.size

            def apply(idx: Int): Node = children(idx)
        }
    }

    def textNodes = children filter {_.isText} map {_.text}

    /**
     * Get an attribute value. Must be invoked on element nodes only.
     *
     * @return the attribute value for the attribute <code>name</code> having the namespace uri <code>uri</code> or
     *         <code>null</code> if there is no such attribute
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement
     */
    def attribute(uri: String, name: String): String

    def attribute(name: String): String = attribute(null, name)

    /**
     * Get the text content. Must be invoked on text nodes only.
     *
     * @return the text content
     * @throws IllegalArgumentException when invoked on a non-text node
     * @see #isText
     */
    def text: String

    /**
     * Get the local name. Must be invoked on element nodes only.
     *
     * @return the local name
     * @throws IllegalArgumentException when invoked on a non-element node
     * @see #isElement
     */
    def name: String

    /**
     * Get the namespace URI.
     *
     * @return the namespace uri
     */
    def namespaceUri: String

    /**
     * @return <code>true</code> if this is an element
     */
    def isElement: Boolean

    /**
     * @return <code>true</code> if this is a text node
     */
    def isText: Boolean

    def siblings: Seq[Node] = parent map {_.children} getOrElse (List(this)) filter {_.isElement}

    def matchesAnyParent(m: Node => Boolean): Boolean = parent.map({p => m(p) || p.matchesAnyParent(m)})
        .getOrElse(false)

    def siblingsOfSameType = {
        val expName = (name, namespaceUri)
        siblings filter {n => n.isElement && (n.name, n.namespaceUri) == expName}
    }

}

abstract class NodeFactory[Orig] {

    implicit def asNode(n: Orig): Node

}
