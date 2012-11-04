package org.rucksac.parser

import org.rucksac.{NodeBrowserRegistry, Node}
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

        // TODO get rid of this method
        private def build(seq: Seq[Node[T]]) = (newBuilder ++= seq).result()

        def @@(matchable: Matchable): Query[T] = {
            def collectNodes(nodes: Seq[Node[T]], include: Boolean): mutable.Builder[Node[T], Query[T]] =
                (newBuilder /: nodes)((builder, node) => {
                    if (include && (matchable.mustFilter || matchable(node))) builder += node
                    builder ++= collectNodes(node.children, true).result()
                })
            val descendants = collectNodes(seq, false).result()
            if (matchable.mustFilter) build(matchable(descendants)) else descendants
        }

        def @@>(matchable: Matchable) = {
            val children = ((newBuilder /: seq)((builder, node) =>
                if (matchable.mustFilter)
                    builder ++= node.children
                else
                    (builder /: node.children)((b, child) => if (matchable(child)) b += child else b)
            )).result()
            if (matchable.mustFilter) build(matchable(children)) else children
        }

        def @@+(matchable: Matchable) = {
            val siblings = ((newBuilder /: seq)((builder, node) => {
                val all = node.siblings
                val index = all.indexOf(node)
                if (index < all.size - 1) {
                    val sib = all(index + 1)
                    if (matchable.mustFilter || matchable(sib)) builder += sib
                }
                builder
            })).result()
            if (matchable.mustFilter) build(matchable(siblings)) else siblings
        }

        def @@~(matchable: Matchable) = {
            val siblings = ((newBuilder /: seq)((builder, node) => {
                val all = node.siblings
                val sibs = all.drop(all.indexOf(node) + 1)
                builder ++= (if (matchable.mustFilter) sibs else sibs.filter(matchable(_)))
            })).result().distinct
            if (matchable.mustFilter) build(matchable(siblings)) else siblings
        }

        def filter(expression: String) = {
            val matchable = asMatchable(expression)
            build(
                if (matchable.mustFilter && matchable.usesCombinators)
                    seq intersect build(seq.map(_.root).distinct).@@(matchable)
                else
                    matchable(seq)
            )
        }

    }

    object Query {

        def apply[T]() = new Query[T](List())

        def apply[T](n: T) = new Query[T](List(Node(n, None, NodeBrowserRegistry(n))))

        def apply[T](p: Matchable, n: T): Query[T] = new Query[T](List(Node(n, None, NodeBrowserRegistry(n)))).@@(p)

    }

    val $ = Query

    implicit def asMatchable(expression: String): Matchable = new Parser().parse(expression)

}
