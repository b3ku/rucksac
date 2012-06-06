package org.rucksac.sac

import org.w3c.css.sac.{ElementSelector, Condition, AttributeCondition}


/**
 * @author Oliver Becker
 * @since 19.05.12
 */

class AttributeConditionImpl(cType: Short, nsUri: String, name: String, specified: Boolean, value: String)
  extends ConditionImpl with AttributeCondition {

  def getConditionType = cType

  def getNamespaceURI = nsUri

  def getLocalName = name

  def getSpecified = specified

  def getValue = value

  override def toString = cType match {
  case Condition.SAC_ID_CONDITION => "#" + value
  case Condition.SAC_CLASS_CONDITION => "." + value
  case Condition.SAC_ATTRIBUTE_CONDITION => "[" + nameToString + (if (value == null) "]" else  "=" + value + "]")
  case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION => opToString("~=")
  case Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION => opToString("|=")
  case SacExtensions.SAC_ATTRIBUTE_DOLLAR_CONDITION => opToString("$=")
  case SacExtensions.SAC_ATTRIBUTE_HAT_CONDITION => opToString("^=")
  case SacExtensions.SAC_ATTRIBUTE_STAR_CONDITION => opToString("*=")
  case _ => "attrCondition(" + cType + "," + nameToString + "," + value + ")"
  }

  private def nameToString = (if (nsUri != null) "{" + nsUri + "}|" else "") + name

  private def opToString(op: String) = "[" + nameToString + op + value + "]"
}


object AttributeConditionImpl {

  def createIdCondition(id: String): AttributeCondition =
    new AttributeConditionImpl(Condition.SAC_ID_CONDITION, null, null, true, id)

  def createClassCondition(styleClass: String): AttributeCondition =
    new AttributeConditionImpl(Condition.SAC_CLASS_CONDITION, null, null, true, styleClass)

  def createAttributeCondition(typeSel: ElementSelector): AttributeCondition =
    createAttributeCondition(typeSel, AttrEqualsComparator, null)

  def createAttributeCondition(typeSel: ElementSelector, op: AttributeComparator, value: String): AttributeCondition =
    new AttributeConditionImpl(
      op match {
      case AttrEqualsComparator => Condition.SAC_ATTRIBUTE_CONDITION
      case AttrTildeComparator => Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION
      case AttrBarComparator => Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION
      case AttrDollarComparator => SacExtensions.SAC_ATTRIBUTE_DOLLAR_CONDITION
      case AttrHatComparator => SacExtensions.SAC_ATTRIBUTE_HAT_CONDITION
      case AttrStarComparator => SacExtensions.SAC_ATTRIBUTE_STAR_CONDITION
      },
      typeSel.getNamespaceURI, typeSel.getLocalName, true, value)

  // TODO remove
  def createDummyCondition: AttributeCondition = new AttributeConditionImpl(Condition.SAC_ATTRIBUTE_CONDITION, null,
    "<dummy>", true, "<dummy>")

}
