package org.rucksac.sac

import org.w3c.css.sac.Selector

/**
 * @author Oliver Becker
 * @since 05.06.12
 */

class SelectorConditionImpl(selector: Selector) extends ConditionImpl {

  def getConditionType = SacExtensions.SAC_ATTRIBUTE_SELECTOR_CONDITION

  override def toString = selector.toString
}
