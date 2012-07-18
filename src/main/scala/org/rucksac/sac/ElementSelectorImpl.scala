package org.rucksac.sac

import org.w3c.css.sac.{Selector, ElementSelector}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

class ElementSelectorImpl(nsUri: String, name: String) extends SelectorImpl with ElementSelector {

  def getSelectorType = Selector.SAC_ELEMENT_NODE_SELECTOR

  def getNamespaceURI = nsUri

  def getLocalName = name

  override def toString = (if (nsUri != null) nsUri + "|" else "*|") + (if (name != null) name else "*")
}
