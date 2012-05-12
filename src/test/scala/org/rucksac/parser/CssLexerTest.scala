package org.rucksac.parser

/**
 * @author Oliver Becker
 * @since 12.05.12
 */

import org.junit.{Assert, Test}
import org.scalatest.junit.JUnitSuite

/**
 * @author Oliver Becker
 * @since 11.05.12
 */

class CssLexerTest extends JUnitSuite {

  case class ParseException(msg: String) extends RuntimeException

  object TestCssLexer extends CssLexer {

    def testParse(input: String): CssToken = parseAll(token, input) match {
    case Success(x, _) => x
    case NoSuccess(msg, _) => throw new ParseException(msg)
    }

    def assertString(input: String, expected: String) {
      val result = testParse(input)
      result match {
      case CssString(_) => Assert.assertEquals(expected, result.chars)
      case _ => Assert.fail()
      }
    }

    def assertIdent(input: String, expected: String) {
      val result = testParse(input)
      result match {
      case CssIdent(_) => Assert.assertEquals(expected, result.chars)
      case _ => Assert.fail()
      }
    }

    def assertHash(input: String, expected: String) {
      val result = testParse(input)
      result match {
      case CssHash(_) => Assert.assertEquals(expected, result.chars)
      case _ => Assert.fail()
      }
    }

    def assertFunction(input: String, expected: String) {
      val result = testParse(input)
      result match {
      case CssFunction(_) => Assert.assertEquals(expected, result.chars)
      case _ => Assert.fail()
      }
    }

    def assertDimension(input: String, expected: String) {
      val result = testParse(input)
      result match {
      case CssDimension(_) => Assert.assertEquals(expected, result.chars)
      case _ => Assert.fail()
      }
    }

    def assertNumber(input: String, expected: String) {
      val result = testParse(input)
      result match {
      case CssNumber(_) => Assert.assertEquals(expected, result.chars)
      case _ => Assert.fail()
      }
    }

    def assertOp(input: String, expected: String) {
      testParse(input) match {
      case CssPlus(_) => Assert.assertEquals(expected, "+")
      case CssGreater(_) => Assert.assertEquals(expected, ">")
      case CssTilde(_) => Assert.assertEquals(expected, "~")
      case CssComma(_) => Assert.assertEquals(expected, ",")
      case _ => Assert.fail()
      }
    }

    def assertPlus(input: String) {
      assertOp(input, "+")
    }

    def assertGreater(input: String) {
      assertOp(input, ">")
    }

    def assertTilde(input: String) {
      assertOp(input, "~")
    }

    def assertComma(input: String) {
      assertOp(input, ",")
    }

  }


  @Test
  def testString() {
    TestCssLexer.assertString("'abcdef'", "'abcdef'")
    TestCssLexer.assertString("\"abcdef\"", "\"abcdef\"")
    TestCssLexer.assertString("'abc\"def'", "'abc\"def'")
    TestCssLexer.assertString("\"abc'''def\"", "\"abc'''def\"")

  }

  @Test(expected = classOf[ParseException])
  def testStringFailure1() {
    TestCssLexer.testParse("'abc")
  }

  @Test(expected = classOf[ParseException])
  def testStringFailure2() {
    TestCssLexer.testParse("abc 'def' xyz")
  }

  @Test(expected = classOf[ParseException])
  def testStringFailure3() {
    TestCssLexer.testParse("\"ab\"c")
  }

  @Test
  def testFunction() {
    TestCssLexer.assertFunction("foo(", "foo");
  }

  @Test
  def testIdent() {
    TestCssLexer.assertIdent("foo", "foo")
    TestCssLexer.assertIdent("-foo-bar", "-foo-bar")
    TestCssLexer.assertIdent("H1", "H1")
  }

  @Test
  def testDimension() {
    TestCssLexer.assertDimension("12px", "12px")
    TestCssLexer.assertDimension("1.5em", "1.5em")
    TestCssLexer.assertDimension("/* 12pt */14pt", "14pt")
  }

  @Test
  def testHash() {
    TestCssLexer.assertHash("#abc", "abc")
    TestCssLexer.assertHash("#123", "123")
    TestCssLexer.assertHash("/*comment*/#_x-y", "_x-y")
  }

  @Test
  def testNumber() {
    TestCssLexer.assertNumber("42", "42")
    TestCssLexer.assertNumber("12.34", "12.34")
    TestCssLexer.assertNumber("/* ignored */98/* ignored */", "98")
  }

  @Test
  def testPlus() {
    TestCssLexer.assertPlus("+")
    TestCssLexer.assertPlus("    +")
    TestCssLexer.assertPlus("/* ignored */  +")
  }

  @Test
  def testGreater() {
    TestCssLexer.assertGreater(">")
    TestCssLexer.assertGreater("    >")
    TestCssLexer.assertGreater("/* ignored */  >")
  }

  @Test
  def testTilde() {
    TestCssLexer.assertTilde("~")
    TestCssLexer.assertTilde("    ~")
    TestCssLexer.assertTilde("/* ignored */  ~")
  }

  @Test
  def testComma() {
    TestCssLexer.assertComma(",")
    TestCssLexer.assertComma("    ,")
    TestCssLexer.assertComma("/* ignored */  ,")
  }

}
