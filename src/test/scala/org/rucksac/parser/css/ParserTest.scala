package org.rucksac.parser.css

import org.junit.{Assert, Test}
import org.rucksac.parser.{DomNodeBrowser, Qualifiable, ElementSelector, Selector}
import org.rucksac.ParseException

/**
 * @author Oliver Becker
 * @since 15.05.12
 */

class ParserTest {

    object Parser {
        def apply() = new Parser(new DomNodeBrowser)
    }

    @Test(expected = classOf[ParseException])
    def testInvalidSyntaxSelector() {
        Parser().parse("div +> p")
    }

    @Test(expected = classOf[ParseException])
    def testInvalidNotSelector() {
        Parser().parse(":not(:not(dummy))")
    }

    @Test(expected = classOf[ParseException])
    def testInvalidPseudoFunctionWithoutArgument() {
        Parser().parse(":func()")
    }

    def assertElementSelector(selector: Selector, localName: String, namespaceUri: String) {
        Assert.assertTrue(selector.getClass == classOf[ElementSelector])
        val element = selector.asInstanceOf[ElementSelector]
        Assert.assertEquals(new Qualifiable(namespaceUri, localName), element.toString)
    }

    @Test
    def testParseCss1() {
        var selectors = Parser().parse("a")
        Assert.assertEquals("*|a", selectors.toString)

        selectors = Parser().parse("a, b")
        Assert.assertEquals("*|a, *|b", selectors.toString)

        selectors = Parser().parse("a + b > c")
        Assert.assertEquals("*|a + *|b > *|c", selectors.toString)

        selectors = Parser().parse("a b c")
        Assert.assertEquals("*|a *|b *|c", selectors.toString)

        selectors = Parser().parse("div p p/* #^@!* */")
        Assert.assertEquals("*|div *|p *|p", selectors.toString)

        selectors = Parser().parse("div * p/* #^@!* */")
        Assert.assertEquals("*|div *|* *|p", selectors.toString)
    }

    @Test
    def testParseCss2() {
        var result = Parser().parse("div.main.user > .dummy[name='foo']")
        Assert.assertEquals("*|div.main.user > *|*.dummy[*|name=foo]", result.toString)

        result = Parser().parse("div.main.user .dummy[name='foo']")
        Assert.assertEquals("*|div.main.user *|*.dummy[*|name=foo]", result.toString)
    }

    @Test
    def testParseCss3() {
        val result = Parser().parse("h1, h2.foo, h3")
        Assert.assertEquals("*|h1, *|h2.foo, *|h3", result.toString)
    }

    @Test
    def testParseCss4() {
        val result = Parser().parse("h1+h2, h1 + #foo")
        Assert.assertEquals("*|h1 + *|h2, *|h1 + *|*#foo", result.toString)
    }

    @Test
    def testParseCss5() {
        val result = Parser().parse("h1~h2, h1 ~ :foo")
        Assert.assertEquals("*|h1 ~ *|h2, *|h1 ~ *|*:foo", result.toString)
    }

    @Test
    def testParseCss6() {
        val result = Parser().parse("*.warning")
        Assert.assertEquals("*|*.warning", result.toString)
    }

    @Test
    def testParseCss7() {
        val result = Parser().parse("*#id")
        Assert.assertEquals("*|*#id", result.toString)
    }

    @Test
    def testParseCss8() {
        val result = Parser().parse("[att]")
        Assert.assertEquals("*|*[*|att]", result.toString)
    }

    @Test
    def testParseCss9() {
        val result = Parser().parse( """object[type^="image/"]""")
        Assert.assertEquals( """*|object[*|type^=image/]""", result.toString)
    }

    @Test
    def testParseCss10() {
        val result = Parser().parse( """a[href$=".html"]""")
        Assert.assertEquals( """*|a[*|href$=.html]""", result.toString)
    }

    @Test
    def testParseCss11() {
        val result = Parser().parse( """p[title*="hello"]""")
        Assert.assertEquals( """*|p[*|title*=hello]""", result.toString)
    }

    @Test
    def testParseCss12() {
        val result = Parser().parse( """a[hreflang|="en"]""")
        Assert.assertEquals( """*|a[*|hreflang|=en]""", result.toString)
    }

    @Test
    def testParseCss13() {
        val result = Parser().parse("a[hreflang=en]")
        Assert.assertEquals("*|a[*|hreflang=en]", result.toString)
    }

    @Test
    def testParseCss14() {
        val result = Parser().parse( """a[rel~="copyright"]""")
        Assert.assertEquals( """*|a[*|rel~=copyright]""", result.toString)
    }

    @Test
    def testParseCss15() {
        val result = Parser().parse("[foo|att=val]")
        Assert.assertEquals("*|*[foo|att=val]", result.toString)
    }

    @Test
    def testParseCss16() {
        val result = Parser().parse("[*|att]")
        Assert.assertEquals("*|*[*|att]", result.toString)
    }

    @Test
    def testParseCss17() {
        val result = Parser().parse("[|att]")
        Assert.assertEquals("*|*[|att]", result.toString)
    }

    @Test
    def testParseCss18() {
        val result = Parser().parse("input:checked:first-child")
        Assert.assertEquals("*|input:checked:first-child", result.toString)
    }

    @Test
    def testParseCss19() {
        val result = Parser().parse(":checkbox")
        Assert.assertEquals("*|*:checkbox", result.toString)
    }

    @Test
    def testParseCss20() {
        // TODO differentiation between pseudo classes and pseudo elements
        val result = Parser().parse("p::first-line span::first-letter")
        Assert.assertEquals("*|p:first-line *|span:first-letter", result.toString)
    }

    @Test
    def testParseCss21() {
        val result = Parser().parse("#bar:not(div)")
        Assert.assertEquals("*|*#bar:not(*|div)", result.toString)
    }

    @Test
    def testParseCss22() {
        val result = Parser().parse("html|*:not(:link):not(:visited)")
        Assert.assertEquals("html|*:not(:link):not(:visited)", result.toString)
    }

    @Test
    def testParseCss23() {
        val result = Parser().parse("html|tr:nth-child(-n+6)")
        Assert.assertEquals("html|tr:nth-child(-n + 6)", result.toString)
    }

    @Test
    def testParseCss24() {
        val result = Parser().parse(":nth-child( +3n - 2 )")
        Assert.assertEquals("*|*:nth-child(+ 3n - 2)", result.toString)
    }

    @Test
    def testParseCss25() {
        val result = Parser().parse(":lang(fr-be) > q")
        Assert.assertEquals("*|*:lang(fr-be) > *|q", result.toString)
    }

    @Test
    def testParseCss26() {
        val result = Parser().parse(":not(:notX(x))")
        Assert.assertEquals("*|*:not(:notX(x))", result.toString)
    }

    @Test
    def testParseCss27() {
        val result = Parser().parse("a#id.class.clazz:hidden[rel=foo]")
        Assert.assertEquals("*|a#id.class.clazz:hidden[*|rel=foo]", result.toString)
    }

}
