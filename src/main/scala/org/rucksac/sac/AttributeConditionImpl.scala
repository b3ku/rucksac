package org.rucksac.sac

import org.w3c.css.sac.{Condition, AttributeCondition}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

case class AttributeConditionImpl(cType: Short, nsUri: String, name: String, specified: Boolean, value: String)
  extends ConditionImpl with AttributeCondition {

  def getConditionType = cType

  def getNamespaceURI = nsUri

  def getLocalName = name

  def getSpecified = specified

  def getValue = value

}


object AttributeConditionImpl {

  def createIdCondition(id: String): AttributeCondition =
    new AttributeConditionImpl(Condition.SAC_ID_CONDITION, null, null, true, id)

  def createClassCondition(styleClass: String): AttributeCondition =
    new AttributeConditionImpl(Condition.SAC_CLASS_CONDITION, null, null, true, styleClass)

  // TODO remove
  def createDummyCondition: AttributeCondition = new AttributeConditionImpl(Condition.SAC_ATTRIBUTE_CONDITION, null,
    null, true, null)

}
