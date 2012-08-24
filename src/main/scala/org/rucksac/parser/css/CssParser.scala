package org.rucksac.parser.css

import util.parsing.combinator.syntactical.StdTokenParsers
import org.rucksac.parser._
import org.rucksac.{NodeMatcherRegistry, ParseException, NodeBrowser}
import collection.mutable.ListBuffer

/**
 * A parser for the CSS selectors level 3 grammar
 *
 * @see http://www.w3.org/TR/selectors/#w3cselgrammar
 * @author Oliver Becker
 * @author Andreas Kuhrwahl
 * @since 13.05.12
 */

class CssParser(registry: NodeMatcherRegistry) extends StdTokenParsers {

    type Tokens = Lexer
    val lexical = new Tokens(registry)

    private case class NextSelector(combinator: CombinatorType, selector: Selector)

    // Helpers
    private def selector_sequence(sel: Selector, list: List[NextSelector]) =
        (sel /: list)((s, next) => new SelectorCombinatorSelector(s, next.combinator, next.selector))

    private def condition_combinator(conditions: List[Condition]) =
        (conditions.head /: conditions.tail)(new CombinatorCondition(_, _))

    private def attribute_operations = {
        val ops: Iterable[String] = List("~=", "^=", "$=", "*=", "|=") ++ registry.getSupportedAttributeOperations
        (keyword("=") /: ops)((x, y) => y | x)
    }

    private def combinators = {
        val ops: Iterable[String] = List(">", "~") ++ registry.getSupportedSelectorCombinators
        (keyword("+") /: ops)((x, y) => y | x)
    }

    // Grammar
    protected def s = elem("whitespace", _.isInstanceOf[lexical.WhiteSpace]) ^^ {_ => " "}
    protected def comma = opt(s) ~> "," <~ opt(s)
    protected def dimension = elem("dimension", _.isInstanceOf[lexical.Dimension])

    def selectors_group = repsep(selector, comma)

    def selector = simple_selector_sequence ~ rep(combinator ~ simple_selector_sequence ^^ {
        case comb ~ sel => NextSelector(comb, sel)
    }) ^^ {
        case seq ~ list => selector_sequence(seq, list)
    }

    def combinator = ((opt(s) ~> combinators <~ opt(s)) | s) ^^ CombinatorType

    def simple_selector_sequence = (type_selector | universal) ~ rep(condition) ^^ {
        case sel ~ conditions => conditions.size match {
            case 0 => sel
            case _ => new ConditionalSelector(sel, condition_combinator(conditions))
        }
    } | rep1(condition) ^^ {
        case conditions => new ConditionalSelector(Any, condition_combinator(conditions))
    }

    def namespace_prefix = opt(ident | "*") <~ "|" ^^ {
        case Some(x) => x
        case None => ""
    }

    protected def qualified_name = opt(namespace_prefix) ~ ident ^^ {
        case Some(prefix) ~ name => new Qualifiable(prefix, name)
        case None ~ name => new Qualifiable(null, name)
    }

    def type_selector = qualified_name ^^ {case Qualifiable(prefix, name) => new ElementSelector(prefix, name)}

    def universal = opt(namespace_prefix) <~ "*" ^^ {
        case Some(prefix) => new ElementSelector(prefix, null)
        case None => Any
    }

    def condition = hash | styleClass | attribute | negation | pseudo

    def hash = "#" ~> ident ^^ {s => new AttributeCondition(null, "id", s, "#")}

    def styleClass = "." ~> ident ^^ {s => new AttributeCondition(null, "class", s, ".")}

    def attribute = "[" ~> attribute_name ~ opt(attribute_operation ~ attribute_value) <~ "]" ^^ {
        case name ~ Some(op ~ value) => new AttributeCondition(name.uri, name.localName, value, op)
        case name ~ None => new AttributeCondition(name.uri, name.localName, null, null)
    }

    protected def attribute_name = opt(s) ~> qualified_name <~ opt(s)
    protected def attribute_operation = attribute_operations <~ opt(s)
    protected def attribute_value = (ident | stringLit) <~ opt(s)

    def negation = ":not(" ~> opt(s) ~> negation_arg <~ opt(s) <~ ")" ^^ {new NegativeCondition(_)}

    def negation_arg = type_selector ^^ {sel => new SelectorCondition(sel)} |
        universal ^^ {sel => new SelectorCondition(sel)} | hash | styleClass | attribute | pseudo

    def pseudo = ":" ~> opt(":") ~>
        (functional_pseudo | ident ^^ {name => new PseudoClassCondition(name)})

    def functional_pseudo = (ident <~ "(" <~ opt(s)) ~ expression <~ ")" ^^ {
        case f ~ e => new PseudoFunctionCondition(f, e)
    }

    def expression = rep1(("+" | "-" | dimension | numericLit | stringLit | ident) <~ opt(s)) ^^ {
        case expressions => expressions.mkString(" ")
    }

    def parse(s: String) = {
        val tokens = new lexical.Scanner(s)
        phrase(selectors_group)(tokens)
    } match {
        case Success(x, _) => new SelectorList(x)
        case NoSuccess(msg, _) => throw new ParseException(msg)
    }

}

class SelectorList(selectors: List[Selector]) {

    import scala.collection.JavaConversions._

    def filter[T](node: T, browser: NodeBrowser[T]) = {
        val matches = new ListBuffer[T]
        def applySelector(node: T, sel: Selector) {
            if (sel(node, browser)) matches += node
            val children: Iterable[T] = browser.children(node)
            children.foreach({n => applySelector(n, sel)})
        }
        selectors.foreach({s => applySelector(node, s)})
        matches
    }

    override def toString = selectors mkString ", "

}
