package org.rucksac.parser

import org.rucksac.Node
import collection._
import mutable.ArrayBuffer

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */

package object css {

    class Query[T](seq: Seq[Node[T]]) extends IndexedSeq[Node[T]] with SeqLike[Node[T], Query[T]] {

        def length = seq.size

        def apply(index: Int): Node[T] = seq(index)

        def apply(): Seq[T] = seq map(_())

        override def newBuilder: mutable.Builder[Node[T], Query[T]] =
            new mutable.Builder[Node[T], Query[T]] {
                def emptyList() = new ArrayBuffer[Node[T]]()

                var current = emptyList()

                def +=(elem: Node[T]) = {
                    current += elem
                    this
                }

                def clear() { current = emptyList() }
                def result() = new Query(current)
            }

        def findAll(predicate: Node[T] => Boolean): Query[T] = {
            def allMatches(query: Seq[Node[T]]): List[Node[T]] = (List[Node[T]]() /: query)((matches, node) => {
                var result = matches
                if (predicate(node)) {
                    result = matches :+ node
                }
                result ++ allMatches(node.children)
            })
            new Query(allMatches(this))
        }

    }

    object Query {

        def apply[T]() = new Query[T](List())

        def apply[T](n: T) = new Query[T](List(Node(n, None)))

        def apply[T](p: Node[T] => Boolean, n: T) = new Query[T](List(Node(n, None))).findAll(p)

    }

    val $ = Query

    implicit def asPredicate(sel: String): Node[_] => Boolean = {
        val selectors = new Parser().parse(sel)
        (node => (false /: selectors)(_ || _(node)))
    }

}
