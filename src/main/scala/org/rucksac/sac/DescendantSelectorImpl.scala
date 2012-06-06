package org.rucksac.sac

import org.w3c.css.sac.{SimpleSelector, Selector, DescendantSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

class DescendantSelectorImpl(selectorType: Short, ancestor: Selector, descendant: SimpleSelector)
  extends SelectorImpl with DescendantSelector {

  def getSelectorType = selectorType

  def getAncestorSelector = ancestor

  def getSimpleSelector = descendant

  override def toString = ancestor + (selectorType match {
  case Selector.SAC_CHILD_SELECTOR => " > "
  case Selector.SAC_DESCENDANT_SELECTOR => " "
  case _ => throw new IllegalArgumentException
  }) + descendant
}


object DescendantSelectorImpl {

  def createDescendantSelector(ancestor: Selector, descendant: SimpleSelector) =
    new DescendantSelectorImpl(Selector.SAC_DESCENDANT_SELECTOR, ancestor, descendant)

  def createChildSelector(ancestor: Selector, descendant: SimpleSelector) =
    new DescendantSelectorImpl(Selector.SAC_CHILD_SELECTOR, ancestor, descendant)

}
