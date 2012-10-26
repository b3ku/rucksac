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

        private def build(seq: Seq[Node[T]]) = (newBuilder /: seq)(_ += _).result()

        private def filterIfNecessary(matchable: Matchable, nodes: Seq[Node[T]]) =
            // TODO rewrite @@, @@>, @@+ and @@~ by using newBuilder instead of constructing List() in the first place
            build(if (matchable.mustFilter) matchable(nodes) else nodes)

        def findAll(matchable: Matchable) = {
            def collectNodes(nodes: Seq[Node[T]], include: Boolean): List[Node[T]] =
                (nodes :\ List[Node[T]]())((node, collected) => {
                    val current = collectNodes(node.children, true) ::: collected
                    if (include && (matchable.mustFilter || matchable(node))) node :: current else current
                })
            filterIfNecessary(matchable, collectNodes(seq, false))
        }

        def findChildren(matchable: Matchable) = {
            val collected = (seq :\ List[Node[T]]())((node, children) =>
                if (matchable.mustFilter)
                    node.children ++: children
                else
                    (node.children :\ children)((child, currentChildren) =>
                        if (matchable(child)) child :: currentChildren else currentChildren))
            filterIfNecessary(matchable, collected)
        }

        def findAdjacentSiblings(matchable: Matchable) = {
            val collected = (seq :\ List[Node[T]]())((node, siblings) => {
                val all = node.siblings
                val index = all.indexOf(node)
                if (index < all.size - 1) {
                    val sib = all(index + 1)
                    if (matchable.mustFilter || matchable(sib)) sib :: siblings else siblings
                } else siblings
            })
            filterIfNecessary(matchable, collected)
        }

        def findGeneralSiblings(matchable: Matchable) = {
            val collected = (seq :\ List[Node[T]]())((node, siblings) => {
                val all = node.siblings
                val sibs = all.drop(all.indexOf(node) + 1)
                val newSibs = if (matchable.mustFilter)
                    sibs
                else
                    (sibs :\ List[Node[T]]())((sib, currentSibs) =>
                        if (matchable(sib)) sib :: currentSibs else currentSibs)
                (newSibs :\ siblings)((sib, currentSibs) =>
                    if (currentSibs.contains(sib)) currentSibs else sib :: currentSibs)
            })
            filterIfNecessary(matchable, collected)
        }

        def filter(expression: String) = build(asMatchable(expression).apply(seq))

    }

    object Query {

        def apply[T]() = new Query[T](List())

        def apply[T](n: T) = new Query[T](List(Node(n, None, NodeBrowserRegistry(n))))

        def apply[T](p: Matchable, n: T): Query[T] = new Query[T](List(Node(n, None, NodeBrowserRegistry(n)))).findAll(p)

    }

    val $ = Query

    implicit def asMatchable(expression: String): Matchable = new Parser().parse(expression)

}
