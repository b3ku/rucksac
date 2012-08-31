package org.rucksac.parser.css

import util.parsing.combinator.lexical.Lexical
import util.parsing.combinator.token.StdTokens
import util.parsing.combinator.RegexParsers
import scala.util.parsing.input.CharArrayReader.EofCh
import collection.immutable.Set
import util.Sorting
import org.rucksac.matcher.NodeMatcherRegistry

/**
 * @author Andreas Kuhrwahl
 * @since 15.08.12
 */

trait CssTokens extends StdTokens {

    case class WhiteSpace(chars: String) extends Token {
        override def toString = "whitespace"
    }

    case class Dimension(chars: String) extends Token {override def toString = chars}

}

class Lexer extends Lexical with RegexParsers with CssTokens {

    override type Elem = Char
    override val whiteSpace = """(\/\*[^*]*\*+([^/*][^*]*\*+)*\/)*""".r

    private val keywords =
        Set[String](":not(", ",", ">", "+", "-", "#", ".", ":", "(", ")", "[", "]", "=", "~=", "^=", "$=", "*=", "|=",
            "|", "*", "~") ++ NodeMatcherRegistry().attributeOperations.keySet ++
            NodeMatcherRegistry().selectorCombinators.keySet

    def whitespace = this.whiteSpace

    lazy val unicode  = """(\\[0-9a-f]{1,6}(\r\n|[ \n\r\t\f])?)"""
    lazy val escape   = "(" + unicode + """|\\[^\n\r\f0-9a-f])"""
    lazy val nonascii = """[^\00-\0177]"""
    lazy val nmchar   = "([_a-z0-9-]|" + nonascii + "|" + escape + ")"
    lazy val nmstart  = "([_a-z]|" + nonascii + "|" + escape + ")"
    lazy val nl       = """(\n|\r\n|\r|\f)"""
    lazy val string1  = """(\"([^\n\r\f\\"]|\\""" + nl + "|" + nonascii + "|" + escape + """)*\")"""
    lazy val string2  = """(\'([^\n\r\f\\']|\\""" + nl + "|" + nonascii + "|" + escape + """)*\')"""

    lazy val ident  = "(?i)([-]?" + nmstart + nmchar + "*)"
    lazy val num    = """([0-9]*\.[0-9]+|[0-9]+)"""
    lazy val string = "(" + string1 + "|" + string2 + ")"
    lazy val s      = """[ \t\r\n\f]+"""

    def token = (ident.r ^^ Identifier
        | (num + ident).r ^^ Dimension
        | num.r ^^ NumericLit
        | string.r ^^ {s => StringLit(s.substring(1, s.length - 1))}
        | s.r ^^ WhiteSpace
        | EofCh ^^^ EOF
        | '\'' ~> failure("unclosed string literal")
        | '\"' ~> failure("unclosed string literal")
        | keyword
        | failure("illegal character"))

    private val keyword: Parser[Token] = {

        def parseKeyword(s: String): Parser[Token] = accept(s.toList) ^^ {_ => Keyword(s)}

        val k = new Array[String](keywords.size)
        keywords.copyToArray(k, 0)
        Sorting.quickSort(k)
        (k.toList map parseKeyword).foldRight(failure("no matching keyword"): Parser[Token])((x, y) => y | x)
    }

}
