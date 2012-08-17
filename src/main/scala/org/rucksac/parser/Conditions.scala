package org.rucksac.parser

import org.rucksac.NodeBrowser

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait Condition extends Matchable

case class ConditionType(op: String) {override def toString = op}

case class PseudoFunction(expression: String) extends ConditionType(":")

final class NegativeCondition(con: Condition) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = !con.matches(node, browser)

    override def toString = ":not(" + con + ")"

}

final class SelectorCondition(sel: Selector) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = sel.matches(node, browser)

    override def toString = sel.toString

}

final class CombinatorCondition(first: Condition, second: Condition) extends Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = first.matches(node, browser) & second.matches(node, browser)

    override def toString = first.toString + second.toString

}

final class AttributeCondition(prefix: String, localName: String, value: String, condition: ConditionType)
    extends Qualifiable(prefix, localName) with Condition {

    def matches[T](node: T, browser: NodeBrowser[T]) = true

    override def toString = condition match {
        case ConditionType("#") => "#" + value
        case ConditionType(".") => "." + value
        case f: PseudoFunction => ":" + value + "(" + f.expression + ")"
        case ConditionType(":") => ":" + value
        case _ => "[" + super.toString + (if (value == null) "" else condition + value) + "]"
    }

}

