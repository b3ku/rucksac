package org.rucksac.parser

import org.rucksac.Node
import collection._
import mutable.ArrayBuffer

/**
 * @author Andreas Kuhrwahl
 * @since 17.08.12
 */

package object css {

    class QueryPredicate[T](expression: String) extends (Node[T] => Boolean) {

        private val selectors = new Parser().parse(expression)

        private lazy val mustFilter = (false /: selectors)(_ || _.mustFilter)

        def apply(node: Node[T]): Boolean = (false /: selectors)(_ || _(node))

        def filter(nodes: Seq[Node[T]]): Seq[Node[T]] = nodes intersect (List[Node[T]]() /: selectors)(_ ++ _(nodes))

        // TODO get rid of mustFilter and finally of class QueryPredicate
//        def apply(nodes: Seq[Node[T]]): Query[T] = {
//            def collectNodes(nodes: Seq[Node[T]]): (List[Node[T]], List[Node[T]]) =
//                (nodes :\ (List[Node[T]](), List[Node[T]]()))((node, pair) => {
//                    val (all, matches) = pair
//                    val (allChildren, matchChildren) = collectNodes(node.children)
//                    (node :: allChildren ::: all,
//                        if (apply(node)) node :: matchChildren ::: matches else matchChildren ::: matches)
//                })
//            val (_, matches) = collectNodes(nodes)
//            new Query(matches)
//        }

        def apply(nodes: Seq[Node[T]]): Query[T] = {
            def collectNodes(nodes: Seq[Node[T]]): List[Node[T]] =
                (nodes :\ List[Node[T]]())((node, collected) => {
                    val current = collectNodes(node.children) ::: collected
                    if (mustFilter || apply(node)) node :: current else current
                })
            val collected = collectNodes(nodes)
            new Query(if (mustFilter) filter(collected) else collected)
        }

    }

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

        def findAll(predicate: String): Query[T] = findAll(new QueryPredicate[T](predicate))

        def findAll(predicate: QueryPredicate[T]): Query[T] = predicate(seq)

    }

    object Query {

        def apply[T]() = new Query[T](List())

        def apply[T](n: T) = new Query[T](List(Node(n, None)))

        def apply[T](p: QueryPredicate[T], n: T): Query[T] = new Query[T](List(Node(n, None))).findAll(p)

        def apply[T](p: String, n: T): Query[T] = apply(new QueryPredicate[T](p), n)

    }

    val $ = Query

    implicit def asDefaultPredicate[T](sel: String): Node[T] => Boolean = {
        new QueryPredicate(sel)
    }

    implicit def asQueryPredicate(sel: String): QueryPredicate[_] = {
        new QueryPredicate(sel)
    }

}
