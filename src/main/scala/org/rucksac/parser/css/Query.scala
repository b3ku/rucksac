package org.rucksac.parser

import collection.immutable._
import org.rucksac.Node

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */

package object css {

    case class Query[T](node: Node[T]) extends IndexedSeq[Query[T]] {

        private val children = node.children

        def length = children.size

        def apply(idx: Int): Query[T] = Query[T](children(idx))

        def apply() = node()

        def findAll(p: Query[T] => Boolean): List[Query[T]] = {
            def allMatches(query: Query[T]): List[Query[T]] = (List[Query[T]]() /: query)((matches, q) => {
                var result = matches
                if (p(q)) {
                    result = matches :+ q
                }
                result ++ allMatches(q)
            })
            allMatches(this)
        }

    }

    object $ {

        def apply[T](n: T) = Query[T](Node(n, None))

        def apply[T](p: Query[T] => Boolean, n: T) = Query[T](Node(n, None)).findAll(p)

    }

    implicit def asPredicate(sel: String): Query[_] => Boolean = {
        val selectors = new Parser().parse(sel)
        (q => (false /: selectors)(_ || _(q.node)))
    }

}
