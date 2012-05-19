package org.rucksac.sac

import org.w3c.css.sac.{SimpleSelector, Selector, DescendantSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

case class DescendantSelectorImpl(ancestor: Selector, descendant: SimpleSelector)
  extends SelectorImpl with DescendantSelector {

  def getSelectorType = Selector.SAC_DESCENDANT_SELECTOR

  def getAncestorSelector = ancestor

  def getSimpleSelector = descendant

}
