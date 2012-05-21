package org.rucksac.sac

import org.w3c.css.sac.{SimpleSelector, Selector, DescendantSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

case class DescendantSelectorImpl(selectorType: Short, ancestor: Selector, descendant: SimpleSelector)
  extends SelectorImpl with DescendantSelector {

  def getSelectorType = selectorType

  def getAncestorSelector = ancestor

  def getSimpleSelector = descendant

}

object DescendantSelectorImpl {

  def createDescendantSelector(ancestor: Selector, descendant: SimpleSelector) =
    new DescendantSelectorImpl(Selector.SAC_DESCENDANT_SELECTOR, ancestor, descendant)

  def createChildSelector(ancestor: Selector, descendant: SimpleSelector) =
    new DescendantSelectorImpl(Selector.SAC_CHILD_SELECTOR, ancestor, descendant)

}
