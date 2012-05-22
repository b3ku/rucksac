package org.rucksac.sac

import org.w3c.css.sac.{Selector, SelectorList}


/**
 * @author Oliver Becker
 * @since 22.05.12
 */

case class SelectorListImpl(selectorList: List[Selector]) extends SelectorList {

  private val selectorArray = selectorList.toArray

  def getLength = selectorArray.length

  def item(index: Int) = selectorArray(index)

}
