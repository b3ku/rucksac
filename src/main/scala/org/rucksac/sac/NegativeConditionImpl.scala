package org.rucksac.sac

import org.w3c.css.sac.{Condition, NegativeCondition}


/**
 * @author Oliver Becker
 * @since 05.06.12
 */

class NegativeConditionImpl(condition: Condition) extends ConditionImpl with NegativeCondition {

  def getCondition = condition

  def getConditionType = Condition.SAC_NEGATIVE_CONDITION

  override def toString = ":not(" + condition + ")"

}
