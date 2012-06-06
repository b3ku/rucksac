package org.rucksac.sac

import org.w3c.css.sac.{SimpleSelector, Selector, SiblingSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

class SiblingSelectorImpl(selectorType: Short, selector: Selector, sibling: SimpleSelector) extends SelectorImpl with SiblingSelector {

  def getNodeType = SiblingSelector.ANY_NODE

  def getSelectorType = selectorType

  def getSelector = selector

  def getSiblingSelector = sibling

  override def toString = selector + (selectorType match {
  case Selector.SAC_DIRECT_ADJACENT_SELECTOR => "+"
  case SacExtensions.SAC_GENERAL_SIBLING_SELECTOR => "~"
  }) + sibling
}

object SiblingSelectorImpl {

  def createDirectAdjacentSibling(selector: Selector, sibling: SimpleSelector) =
    new SiblingSelectorImpl(Selector.SAC_DIRECT_ADJACENT_SELECTOR, selector, sibling)

  def createGeneralSibling(selector: Selector, sibling: SimpleSelector) =
    new SiblingSelectorImpl(SacExtensions.SAC_GENERAL_SIBLING_SELECTOR, selector, sibling)

}
