package org.rucksac.parser

import util.parsing.combinator.RegexParsers
import org.w3c.css.sac.{NegativeCondition, AttributeCondition, SelectorList, ElementSelector, CombinatorCondition, Condition, SimpleSelector, Selector}
import org.rucksac.sac.{SelectorConditionImpl, NegativeConditionImpl, AttributeComparator, AttrHatComparator, AttrStarComparator, AttrDollarComparator, AttrTildeComparator, AttrBarComparator, AttrEqualsComparator, GeneralSiblingCombinator, AdjacentSiblingCombinator, ChildCombinator, DescendantCombinator, SelectorCombinator, SelectorListImpl, SiblingSelectorImpl, DescendantSelectorImpl, AttributeConditionImpl, CombinatorConditionImpl, ConditionalSelectorImpl, ElementSelectorImpl}

/**
 * A parser for the CSS selectors level 3 grammar
 *
 * @see http://www.w3.org/TR/selectors/#w3cselgrammar
 * @author Oliver Becker
 * @since 13.05.12
 */

class CssParser extends RegexParsers with CssTokens {

  implicit def toLogged(name:String) = new {
//    def !!![T](p:Parser[T]) = log(p)(name)
    def !!![T](p:Parser[T]) = p
  }


  // this is a regex for CSS comments, which will be ignored (thus treated like whitespace)
  // "normal" whitespace will not be ignored! (see definition for s)
  override val whiteSpace = d_comment.r


  // ---- lexical definitions ----

  lazy val d_comment = """(\/\*[^*]*\*+([^/*][^*]*\*+)*\/)"""

  lazy val d_ident = "(?i)([-]?" + d_nmstart + d_nmchar + "*)"

  lazy val d_name = "(" + d_nmchar + "+)"

  lazy val d_nmstart = "([_a-z]|" + d_nonascii + "|" + d_escape + ")"

  lazy val d_nonascii = """[^\00-\0177]"""

  lazy val d_unicode = """(\\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?)"""

  lazy val d_escape = "(" + d_unicode + """|\\[^\n\r\f0-9a-f])"""

  lazy val d_nmchar = "([_a-z0-9-]|" + d_nonascii + "|" + d_escape + ")"

  lazy val d_num = """([0-9]*\.[0-9]+|[0-9]+)"""

  lazy val d_string = "(" + d_string1 + "|" + d_string2 + ")"

  lazy val d_string1 = """(\"([^\n\r\f\\"]|\\""" + d_nl + "|" + d_nonascii + "|" + d_escape + """)*\")"""

  lazy val d_string2 = """(\'([^\n\r\f\\']|\\""" + d_nl + "|" + d_nonascii + "|" + d_escape + """)*\')"""

  lazy val d_nl = """(\n|\r\n|\r|\f)"""

  lazy val d_w = """([ \t\r\n\f]*)"""

  lazy val d_not = """(?i)not\("""


  // ---- lexical rules ----

  lazy val s = """[ \t\r\n\f]+""".r

  lazy val optS = """[ \t\r\n\f]*""".r

  final def not: Parser[CssNot] = (":" + d_not).r ^^ { CssNot }

  final def ident: Parser[CssIdent] = d_ident.r ^^ { CssIdent }

  final def string: Parser[CssString] = d_string.r ^^ { CssString }

  final def function: Parser[CssFunction] = ("""(?!"""+ d_not + """)""" + d_ident + """\(""").r ^^ { CssFunction }

  final def number: Parser[CssNumber] = d_num.r ^^ { CssNumber }

  final def hash: Parser[CssHash] = ("#" + d_name).r ^^ { CssHash }

  final def plus: Parser[CssPlus] = (d_w + "\\+").r ^^ { CssPlus }

  final def greater: Parser[CssGreater] = (d_w + ">").r ^^ { CssGreater }

  final def comma: Parser[CssComma] = (d_w + ",").r ^^ { CssComma }

  final def tilde: Parser[CssTilde] = (d_w + "~").r ^^ { CssTilde }

  final def dimension: Parser[CssDimension] = (d_num + d_ident).r ^^ { CssDimension }


  // -- grammar part ----------------------------------------------------------------------------------------------


  def selectors_group: Parser[SelectorList] = selector ~ rep((comma <~ optS) ~> selector) ^^ {
    case sel ~ list => new SelectorListImpl(sel :: list)
  }

  def selector: Parser[Selector] = simple_selector_sequence ~
    rep(combinator ~ simple_selector_sequence ^^ { case comb ~ sel => NextSelector(comb, sel) }) ^^ {
    case seq ~ list => createSelectorSequence(seq, list)
  }

  private def createSelectorSequence(sel: Selector, list: List[NextSelector]): Selector = {
    list.size match {
    case 0 => sel
    case _ => createSelectorSequence(list.head.op match {
    case DescendantCombinator => DescendantSelectorImpl.createDescendantSelector(sel, list.head.selector)
    case ChildCombinator => DescendantSelectorImpl.createChildSelector(sel, list.head.selector)
    case AdjacentSiblingCombinator => SiblingSelectorImpl.createDirectAdjacentSibling(sel, list.head.selector)
    case GeneralSiblingCombinator => SiblingSelectorImpl.createGeneralSibling(sel, list.head.selector)
    }, list.tail)
    }
  }

  def combinator: Parser[SelectorCombinator] = (
    plus <~ optS ^^ { _ => AdjacentSiblingCombinator }
      | greater <~ optS ^^ { _ => ChildCombinator }
      | tilde <~ optS ^^ { _ => GeneralSiblingCombinator }
      | s ^^ { _ => DescendantCombinator }
    )

  def simple_selector_sequence: Parser[SimpleSelector] = (
    base_selector ~ rep(post_selector) ^^ {
      case base ~ conditions =>
      conditions.size match {
      case 0 => base
      case 1 => new ConditionalSelectorImpl(base, conditions.head)
      case _ => new ConditionalSelectorImpl(base, createCombinatorCondition(conditions))
      }
    }
      | post_selector ~ rep(post_selector) ^^ {
      case condition ~ conditions => {
        val base = new ElementSelectorImpl(null, null)
        conditions.size match {
        case 0 => new ConditionalSelectorImpl(base, condition)
        case _ => new ConditionalSelectorImpl(base, createCombinatorCondition(condition :: conditions))
        }
      }
    }
    )

  private def createCombinatorCondition(list : List[Condition]): CombinatorCondition = {
    assert(list.size > 1)
    new CombinatorConditionImpl(list.head, if (list.size == 2) list.tail.head else createCombinatorCondition(list.tail))
  }

  def base_selector: Parser[ElementSelector] = type_selector | universal

  def post_selector: Parser[Condition] = (
    hashCondition
      | styleClass
      | attrib
      | negation
      | pseudo
    )

  def type_selector: Parser[ElementSelector] = opt(namespace_prefix) ~ ident ^^ {
    case Some(prefix) ~ name => new ElementSelectorImpl(prefix, name.chars)
    case None ~ name => new ElementSelectorImpl(null, name.chars)
  }

  def namespace_prefix: Parser[String] =
    opt(
      ident ^^ { x => x.chars}
        | "*" ^^ { _ => null }
    ) <~ """\|(?!\=)""".r ^^ {
      case Some(x) => x
      case None => ""
    }

  def universal: Parser[ElementSelector] = opt(namespace_prefix) <~ "*" ^^ {
    case Some(prefix) => new ElementSelectorImpl(prefix, null)
    case None => new ElementSelectorImpl(null, null)
  }

  def styleClass: Parser[AttributeCondition] = "." ~> ident ^^ {
    className => AttributeConditionImpl.createClassCondition(className.chars)
  }

  def hashCondition: Parser[AttributeCondition] = hash ^^ {
    id => AttributeConditionImpl.createIdCondition(id.chars)
  }

  def attrib: Parser[AttributeCondition] = ("[" ~> ((optS ~> type_selector) <~ optS)) ~ opt(
    ((( "=" ^^ { _ => AttrEqualsComparator }
      | "~=" ^^ { _ => AttrTildeComparator }
      | "|=" ^^ { _ => AttrBarComparator }
      | "^=" ^^ { _ => AttrHatComparator }
      | "$=" ^^ { _ => AttrDollarComparator }
      | "*=" ^^ { _ => AttrStarComparator }
      ) <~ optS) ~ (ident | string) ^^ { case op ~ value => AttrOperator(op, value.chars)} ) <~ optS
  ) <~ "]" ^^ {
    case typeSel ~ Some(attrOp) => AttributeConditionImpl.createAttributeCondition(typeSel, attrOp.op, attrOp.value)
    case typeSel ~ None => AttributeConditionImpl.createAttributeCondition(typeSel)
  }

  def pseudo: Parser[AttributeCondition] = ":" ~ opt(":") ~> (
    functional_pseudo ^^ { func => AttributeConditionImpl.createPseudoClassCondition(func) }
      | ident ^^ { id => AttributeConditionImpl.createPseudoClassCondition(id.chars) } )

  def functional_pseudo: Parser[String] = function ~ optS ~ expression <~ ")" ^^ {
    case f ~ _ ~ e => f.chars + "(" + e + ")"
  }

  def expression: Parser[String] = simple_expression ~ rep(simple_expression) ^^ {
    case simpleEx ~ simpleSeq => (simpleEx :: simpleSeq).mkString(" ")
  }

  def simple_expression: Parser[String] = (
    plus ^^ { _ => "+" }
      | "-"
      | dimension ^^ { d => d.chars }
      | number ^^ { n => n.chars }
      | string ^^ { s => s.chars }
      | ident ^^ { i => i.chars }
    ) <~ optS

  def negation: Parser[NegativeCondition] = not ~> optS ~> negation_arg <~ ")" ^^ {
    arg => new NegativeConditionImpl(arg)
  }

  def negation_arg: Parser[Condition] = (
    type_selector ^^ { sel => new SelectorConditionImpl(sel) }
      | universal ^^ { sel => new SelectorConditionImpl(sel) }
      | hashCondition
      | styleClass
      | attrib
      | pseudo
    )

}


private case class NextSelector(op: SelectorCombinator, selector: SimpleSelector)

private case class AttrOperator(op: AttributeComparator, value: String)
