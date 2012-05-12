package org.rucksac.parser

import util.parsing.combinator.lexical.Scanners
import util.parsing.combinator.RegexParsers

/**
 * Scanner for CSS Level 3
 *
 * @see http://www.w3.org/TR/selectors/#w3cselgrammar
 * @author Oliver Becker
 * @since 11.05.12
 */

class CssLexer extends Scanners with RegexParsers with CssTokens {

  override type Elem = Char

  def token: Parser[CssToken] =
    (string ^^ CssString
      | function ^^ CssFunction
      | ident ^^ CssIdent
      | dimension ^^ CssDimension
      | number ^^ CssNumber
      | hash ^^ CssHash
      | plus ^^ CssPlus
      | greater ^^ CssGreater
      | tilde ^^ CssTilde
      | comma ^^ CssComma
      | s ^^ CssSpace
      )


  def whitespace = "" // unused, CSS explicitly defines whitespace handling

  // this is a regex for CSS comments, which will be ignored (thus treated like whitespace)
  // "normal" whitespace will not be ignored! (see definition for s)
  override val whiteSpace = """\/\*[^*]*\*+([^/*][^*]*\*+)*\/""".r


  // ---- lexical definitions ----

  private final def d_ident = "(?i)([-]?" + d_nmstart + d_nmchar + "*)"

  private final def d_name = "(" + d_nmchar + "+)"

  private final def d_nmstart = "([_a-z]|" + d_nonascii + "|" + d_escape + ")"

  private final def d_nonascii = """[^\00-\0177]"""

  private final def d_unicode = """(\\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?)"""

  private final def d_escape = "(" + d_unicode + """|\\[^\n\r\f0-9a-f])"""

  private final def d_nmchar = "([_a-z0-9-]|" + d_nonascii + "|" + d_escape + ")"

  private final def d_num = """([0-9]*\.[0-9]+|[0-9]+)"""

  private final def d_string = "(" + d_string1 + "|" + d_string2 + ")"

  private final def d_string1 = """(\"([^\n\r\f\\"]|\\""" + d_nl + "|" + d_nonascii + "|" + d_escape + """)*\")"""

  private final def d_string2 = """(\'([^\n\r\f\\']|\\""" + d_nl + "|" + d_nonascii + "|" + d_escape + """)*\')"""

  private final def d_nl = """(\n|\r\n|\r|\f)"""

  private final def d_w = """([ \t\r\n\f]*)"""


  // ---- lexical rules ----

  final def s = """[ \t\r\n\f]+""".r

  final def ident = d_ident.r

  final def string = d_string.r

  final def function = (d_ident + """\(""").r

  final def number = d_num.r

  final def hash = ("#" + d_name).r

  final def plus = (d_w + "\\+").r

  final def greater = (d_w + ">").r

  final def comma = (d_w + ",").r

  final def tilde = (d_w + "~").r

  final def dimension = (d_num + d_ident).r

}
