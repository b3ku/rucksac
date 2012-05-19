package org.rucksac.parser

import util.parsing.combinator.RegexParsers

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

  final def not: Parser[CssToken] = (":" + d_not).r ^^ { CssNot }

  final def ident: Parser[CssToken] = d_ident.r ^^ { CssIdent }

  final def string: Parser[CssToken] = d_string.r ^^ { CssString }

  final def function: Parser[CssToken] = ("""(?!"""+ d_not + """)""" + d_ident + """\(""").r ^^ { CssFunction }

  final def number: Parser[CssToken] = d_num.r ^^ { CssNumber }

  final def hash = ("#" + d_name).r ^^ { CssHash }

  final def plus: Parser[CssToken] = (d_w + "\\+").r ^^ { CssPlus }

  final def greater: Parser[CssToken] = (d_w + ">").r ^^ { CssGreater }

  final def comma: Parser[CssToken] = (d_w + ",").r ^^ { CssComma }

  final def tilde: Parser[CssToken] = (d_w + "~").r ^^ { CssTilde }

  final def dimension: Parser[CssToken] = (d_num + d_ident).r ^^ { CssDimension }


  // -- grammar part ----------------------------------------------------------------------------------------------


  def selectors_group = "sel_group" !!! (selector  ~ rep((comma <~ optS) ~ selector))

  def selector = "selector" !!! simple_selector_sequence ~ rep(combinator ~ simple_selector_sequence)

  def combinator = (
    plus <~ optS
      | greater <~ optS
      | tilde <~ optS
      | s
    )

  def simple_selector_sequence = (
    base_selector ~ rep(post_selector)
      | post_selector ~ rep(post_selector)
    )

  def base_selector = type_selector | universal

  def post_selector = hash | styleClass | attrib | negation | pseudo

  def type_selector = "type_sel" !!! opt(namespace_prefix) ~ ident

  def namespace_prefix = "ns_prefix" !!! opt("ns_ident" !!! ident | "ns_*" !!!  "*") ~ """\|(?!\=)""".r

  def universal = "universal" !!! opt(namespace_prefix) ~ "*"

  def styleClass = "." ~ ident

  def attrib = "attrib" !!! "[" ~ ((optS ~> type_selector) <~ optS) ~ opt(
    ((("=" | "~=" | "|=" | "^=" | "$=" | "*=") <~ optS) ~ (ident | string)) <~ optS
  ) ~ "]"

  def pseudo = ":" ~ opt(":") ~ (functional_pseudo | ident)

  def functional_pseudo = "func_pseudo" !!! function ~ optS ~ expression ~ ")"

  def expression = simple_expression ~ rep(simple_expression)

  def simple_expression = (plus | "-" | dimension | number | string | ident) <~ optS

  def negation = "not" !!! not ~ optS ~ negation_arg ~ ")"

  def negation_arg = (
    "neg_type" !!! type_selector
      | "neg_uni" !!! universal
      | "neg_hash" !!! hash
      | "neg_class" !!! styleClass
      | "neg_att" !!! attrib
      | "neg_pseudo" !!! pseudo
    )

}
