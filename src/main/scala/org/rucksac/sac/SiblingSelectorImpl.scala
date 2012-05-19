package org.rucksac.sac

import org.w3c.css.sac.{SimpleSelector, Selector, SiblingSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

case class SiblingSelectorImpl(selector: Selector, sibling: SimpleSelector) extends SelectorImpl with SiblingSelector {

  def getNodeType = SiblingSelector.ANY_NODE

  def getSelectorType = Selector.SAC_DIRECT_ADJACENT_SELECTOR

  def getSelector = selector

  def getSiblingSelector = sibling

}
