package org.rucksac.parser

import util.parsing.combinator.token.Tokens

/**
 * @author Oliver Becker
 * @since 12.05.12
 */

trait CssTokens extends Tokens {

  abstract class CssToken extends Token

  case class CssIdent(s: String) extends CssToken {
    def chars = s
  }

  case class CssString(s: String) extends CssToken {
    def chars = s
  }

  case class CssNumber(s: String) extends CssToken {
    def chars = s
  }

  case class CssHash(s: String) extends CssToken {
    def chars = s.substring(1) // strip hash character
  }

  case class CssFunction(s: String) extends CssToken {
    def chars = s.substring(0, s.length-1) // strip open parenthesis
  }

  case class CssDimension(s: String) extends CssToken {
    def chars = s
  }

  case class CssPlus(s: String) extends CssToken {
    def chars = "+"
  }

  case class CssGreater(s: String) extends CssToken {
    def chars = ">"
  }

  case class CssTilde(s: String) extends CssToken {
    def chars = "~"
  }

  case class CssComma(s: String) extends CssToken {
    def chars = ","
  }

  case class CssNot(s: String) extends CssToken {
    def chars = "not"
  }

}
