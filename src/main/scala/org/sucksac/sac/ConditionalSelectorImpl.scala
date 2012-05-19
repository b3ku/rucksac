package org.sucksac.sac

import org.w3c.css.sac.{Condition, SimpleSelector, Selector, ConditionalSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

case class ConditionalSelectorImpl(selector: SimpleSelector, condition: Condition) extends SelectorImpl with ConditionalSelector {

  def getSelectorType = Selector.SAC_CONDITIONAL_SELECTOR

  def getSimpleSelector = selector

  def getCondition = condition

}
