package org.rucksac.parser.css

import org.junit.{Assert, Test}
import org.rucksac.ParseException

/**
 * @author Oliver Becker
 * @since 15.05.12
 */

class ParserTest {

    object Parser {
        def parse(q: String) = new Parser().parse(q).toString
    }

    @Test(expected = classOf[ParseException])
    def testInvalidSyntaxSelector() {
        Parser.parse("div +> p")
    }

    @Test(expected = classOf[ParseException])
    def testInvalidNotSelector() {
        Parser.parse(":not(:not(dummy))")
    }

    @Test(expected = classOf[ParseException])
    def testInvalidPseudoFunctionWithoutArgument() {
        Parser.parse(":func()")
    }

    @Test
    def testParseCss1() {
        var result = Parser.parse("a")
        Assert.assertEquals("*|a", result)

        result = Parser.parse("a, b")
        Assert.assertEquals("*|a, *|b", result)

        result = Parser.parse("a + b > c")
        Assert.assertEquals("*|a + *|b > *|c", result)

        result = Parser.parse("a b c")
        Assert.assertEquals("*|a *|b *|c", result)

        result = Parser.parse("div p p/* #^@!* */")
        Assert.assertEquals("*|div *|p *|p", result)

        result = Parser.parse("div * p/* #^@!* */")
        Assert.assertEquals("*|div *|* *|p", result)
    }

    @Test
    def testParseCss2() {
        var result = Parser.parse("div.main.user > .dummy[name='foo']")
        Assert.assertEquals("*|div.main.user > *|*.dummy[*|name=foo]", result)

        result = Parser.parse("div.main.user .dummy[name='foo']")
        Assert.assertEquals("*|div.main.user *|*.dummy[*|name=foo]", result)
    }

    @Test
    def testParseCss3() {
        val result = Parser.parse("h1, h2.foo, h3")
        Assert.assertEquals("*|h1, *|h2.foo, *|h3", result)
    }

    @Test
    def testParseCss4() {
        val result = Parser.parse("h1+h2, h1 + #foo")
        Assert.assertEquals("*|h1 + *|h2, *|h1 + *|*#foo", result)
    }

    @Test
    def testParseCss5() {
        val result = Parser.parse("h1~h2, h1 ~ :foo")
        Assert.assertEquals("*|h1 ~ *|h2, *|h1 ~ *|*:foo", result)
    }

    @Test
    def testParseCss6() {
        val result = Parser.parse("*.warning")
        Assert.assertEquals("*|*.warning", result)
    }

    @Test
    def testParseCss7() {
        val result = Parser.parse("*#id")
        Assert.assertEquals("*|*#id", result)
    }

    @Test
    def testParseCss8() {
        val result = Parser.parse("[att]")
        Assert.assertEquals("*|*[*|att]", result)
    }

    @Test
    def testParseCss9() {
        val result = Parser.parse( """object[type^="image/"]""")
        Assert.assertEquals( """*|object[*|type^=image/]""", result)
    }

    @Test
    def testParseCss10() {
        val result = Parser.parse( """a[href$=".html"]""")
        Assert.assertEquals( """*|a[*|href$=.html]""", result)
    }

    @Test
    def testParseCss11() {
        val result = Parser.parse( """p[title*="hello"]""")
        Assert.assertEquals( """*|p[*|title*=hello]""", result)
    }

    @Test
    def testParseCss12() {
        val result = Parser.parse( """a[hreflang|="en"]""")
        Assert.assertEquals( """*|a[*|hreflang|=en]""", result)
    }

    @Test
    def testParseCss13() {
        val result = Parser.parse("a[hreflang=en]")
        Assert.assertEquals("*|a[*|hreflang=en]", result)
    }

    @Test
    def testParseCss14() {
        val result = Parser.parse( """a[rel~="copyright"]""")
        Assert.assertEquals( """*|a[*|rel~=copyright]""", result)
    }

    @Test
    def testParseCss15() {
        val result = Parser.parse("[foo|att=val]")
        Assert.assertEquals("*|*[foo|att=val]", result)
    }

    @Test
    def testParseCss16() {
        val result = Parser.parse("[*|att]")
        Assert.assertEquals("*|*[*|att]", result)
    }

    @Test
    def testParseCss17() {
        val result = Parser.parse("[|att]")
        Assert.assertEquals("*|*[|att]", result)
    }

    @Test
    def testParseCss18() {
        val result = Parser.parse("input:checked:first-child")
        Assert.assertEquals("*|input:checked:first-child", result)
    }

    @Test
    def testParseCss19() {
        val result = Parser.parse(":checkbox")
        Assert.assertEquals("*|*:checkbox", result)
    }

    @Test
    def testParseCss20() {
        // TODO differentiation between pseudo classes and pseudo elements
        val result = Parser.parse("p::first-line span::first-letter")
        Assert.assertEquals("*|p:first-line *|span:first-letter", result)
    }

    @Test
    def testParseCss21() {
        val result = Parser.parse("#bar:not(div)")
        Assert.assertEquals("*|*#bar:not(*|div)", result)
    }

    @Test
    def testParseCss22() {
        val result = Parser.parse("html|*:not(:link):not(:visited)")
        Assert.assertEquals("html|*:not(:link):not(:visited)", result)
    }

    @Test
    def testParseCss23() {
        val result = Parser.parse("html|tr:nth-child(-n+6)")
        Assert.assertEquals("html|tr:nth-child(-n+6)", result)
    }

    @Test
    def testParseCss24() {
        val result = Parser.parse(":nth-child( +3n  - 2 )")
        Assert.assertEquals("*|*:nth-child(+3n-2)", result)
    }

    @Test
    def testParseCss25() {
        val result = Parser.parse(":lang(fr-be) > q")
        Assert.assertEquals("*|*:lang(fr-be) > *|q", result)
    }

    @Test
    def testParseCss26() {
        val result = Parser.parse(":not(:notX(x))")
        Assert.assertEquals("*|*:not(:notX(x))", result)
    }

    @Test
    def testParseCss27() {
        val result = Parser.parse("a#id.class.clazz:hidden[rel=foo]  .span")
        Assert.assertEquals("*|a#id.class.clazz:hidden[*|rel=foo] *|*.span", result)
    }

}
