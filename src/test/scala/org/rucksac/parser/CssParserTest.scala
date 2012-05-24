package org.rucksac.parser

import org.junit.{Assert, After, Ignore, Test}
import org.w3c.css.sac.{ElementSelector, DescendantSelector, Selector, SelectorList}


/**
 * @author Oliver Becker
 * @since 15.05.12
 */

class CssParserTest {

  case class ParseException(msg: String) extends RuntimeException

  object TestCssParser extends CssParser {

    def testParse(input: String): SelectorList = {
      println("\n===================================================================")
      println("parse: " + input)
      val res = parseAll(selectors_group, input) match {
      case Success(x, _) => x
      case NoSuccess(msg, _) => throw new ParseException(msg)
      }
      println("parsed: " + res)
      println("===================================================================\n")
      res
    }

  }

  @After
  def after() {
    Console.flush()
  }

  @Test
  def testValidSelectors() {
    TestCssParser.testParse("div.main.user > .dummy[name='foo']")
    TestCssParser.testParse("input:checked:first-child")
    TestCssParser.testParse(":checkbox")
    TestCssParser.testParse("#bar:not(div)")
    TestCssParser.testParse("html|tr:nth-child(-n+6)")
    TestCssParser.testParse(":nth-child( +3n - 2 )")
    TestCssParser.testParse("html|*:not(:link):not(:visited)")
    TestCssParser.testParse(":lang(fr-be) > q")
    TestCssParser.testParse("[foo|att=val]")
    TestCssParser.testParse("[*|att]")
    TestCssParser.testParse("[|att]")
    TestCssParser.testParse("[att]")
    TestCssParser.testParse("object[type^=\"image/\"]")
    TestCssParser.testParse("a[href$=\".html\"]")
    TestCssParser.testParse("p[title*=\"hello\"]")
    TestCssParser.testParse("a[hreflang|=\"en\"]")
    TestCssParser.testParse("a[hreflang=en]")
    TestCssParser.testParse("a[rel~=\"copyright\"]")
    TestCssParser.testParse("h1, h2.foo, h3")
    TestCssParser.testParse("h1+h2, h1 + #foo")
    TestCssParser.testParse("h1~h2, h1 ~ :foo")
    TestCssParser.testParse("p::first-line span::first-letter")
    TestCssParser.testParse("*.warning")
    TestCssParser.testParse("*#id")
    TestCssParser.testParse(":not(:notX(x))")
  }

  @Test(expected = classOf[ParseException])
  def testInvalidSyntaxSelector() {
    TestCssParser.testParse("div +> p")
  }

  @Test(expected = classOf[ParseException])
  @Ignore("not illegal by the grammar, but should be according to http://www.w3.org/TR/selectors/#nth-child-pseudo")
  def testNthChildIllegalSpace() {
    TestCssParser.testParse(":nth-child(+ 2n)")
  }

  @Test(expected = classOf[ParseException])
  def testInvalidNotSelector() {
    TestCssParser.testParse(":not(:not(dummy))")
  }

  @Test(expected = classOf[ParseException])
  def testInvalidPseudoFunctionWithoutArgument() {
    TestCssParser.testParse(":func()")
  }

  def assertElementSelector(selector: Selector, localName: String, namespaceUri: String) {
    Assert.assertEquals(Selector.SAC_ELEMENT_NODE_SELECTOR, selector.getSelectorType)
    val element = selector.asInstanceOf[ElementSelector]
    Assert.assertEquals(localName, element.getLocalName)
    Assert.assertEquals(namespaceUri, element.getNamespaceURI)
  }

  @Test
  def testParseCss1() {
    val selectorList = TestCssParser.testParse("div * p/* #^@!* */")
    Assert.assertEquals(1, selectorList.getLength)

    var selector = selectorList.item(0)
    Assert.assertEquals(Selector.SAC_DESCENDANT_SELECTOR , selector.getSelectorType)
    var descendantSelector  = selector.asInstanceOf[DescendantSelector]
    var simple = descendantSelector.getSimpleSelector
    assertElementSelector(simple, "p", null)

    selector = descendantSelector.getAncestorSelector
    Assert.assertEquals(Selector.SAC_DESCENDANT_SELECTOR, selector.getSelectorType)
    descendantSelector = selector.asInstanceOf[DescendantSelector]
    simple = descendantSelector.getSimpleSelector
    assertElementSelector(simple, null, null)

    selector = descendantSelector.getAncestorSelector
    assertElementSelector(selector, "div", null)
  }

}
