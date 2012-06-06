package org.rucksac.sac

import org.w3c.css.sac.{Condition, CombinatorCondition}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

case class CombinatorConditionImpl(cType: Short, first: Condition, second: Condition)
  extends ConditionImpl with CombinatorCondition {

  def this(first: Condition, second: Condition) = this(Condition.SAC_AND_CONDITION, first, second)

  def getConditionType = cType

  def getFirstCondition = first

  def getSecondCondition = second

  override def toString = cType match {
  case Condition.SAC_AND_CONDITION => first.toString + second.toString
  case _ => throw new IllegalArgumentException
  }
}
