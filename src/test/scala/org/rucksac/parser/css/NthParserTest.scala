package org.rucksac.parser.css

import org.junit.{Assert, Test}

/**
 * @author Oliver Becker
 * @since 18.08.12
 */
class NthParserTest {

    def testEvenOdd(matcher: PositionMatcher, odd: Boolean) {
        Assert.assertNotNull(matcher)
        Assert.assertTrue(odd == matcher.matches(1))
        Assert.assertTrue(odd == matcher.matches(3))
        Assert.assertTrue(odd == matcher.matches(5))
        Assert.assertTrue(odd == matcher.matches(127))
        Assert.assertFalse(odd == matcher.matches(0))
        Assert.assertFalse(odd == matcher.matches(2))
        Assert.assertFalse(odd == matcher.matches(4))
        Assert.assertFalse(odd == matcher.matches(6))
        Assert.assertFalse(odd == matcher.matches(42))
    }

    @Test
    def testOdd() {
        testEvenOdd(NthParser.parse("2n+1"), true)
        testEvenOdd(NthParser.parse("2N+1"), true)
        testEvenOdd(NthParser.parse("odd"), true)
        testEvenOdd(NthParser.parse("ODD"), true)
    }

    @Test
    def testEven() {
        testEvenOdd(NthParser.parse("2n+0"), false)
        testEvenOdd(NthParser.parse("2n"), false)
        testEvenOdd(NthParser.parse("-2n"), false)
        testEvenOdd(NthParser.parse("even"), false)
        testEvenOdd(NthParser.parse("EVEN"), false)
    }

    @Test
    def testDefault() {
        val matcher = NthParser.parse("4n+2")
        Assert.assertNotNull(matcher)
        Assert.assertFalse(matcher.matches(0))
        Assert.assertFalse(matcher.matches(1))
        Assert.assertTrue(matcher.matches(2))
        Assert.assertFalse(matcher.matches(3))
        Assert.assertFalse(matcher.matches(4))
        Assert.assertFalse(matcher.matches(5))
        Assert.assertTrue(matcher.matches(6))
        Assert.assertFalse(matcher.matches(7))
        Assert.assertTrue(matcher.matches(42))
    }

    @Test
    def testPlusMinus() {
        def testMatch(matcher: PositionMatcher) {
            Assert.assertNotNull(matcher)
            Assert.assertFalse(matcher.matches(0))
            Assert.assertFalse(matcher.matches(1))
            Assert.assertFalse(matcher.matches(8))
            Assert.assertTrue(matcher.matches(9))
            Assert.assertFalse(matcher.matches(10))
            Assert.assertFalse(matcher.matches(18))
            Assert.assertTrue(matcher.matches(19))
            Assert.assertFalse(matcher.matches(20))
            Assert.assertFalse(matcher.matches(42))
        }
        testMatch(NthParser.parse("10n-1"))
        testMatch(NthParser.parse("10n+9"))
        testMatch(NthParser.parse(" +10n - 1"))
        testMatch(NthParser.parse(" 10n+ 9 "))
    }

    @Test
    def testFactorZero() {
        def testMatch(matcher: PositionMatcher) {
            Assert.assertNotNull(matcher)
            Assert.assertFalse(matcher.matches(0))
            Assert.assertFalse(matcher.matches(1))
            Assert.assertFalse(matcher.matches(2))
            Assert.assertTrue(matcher.matches(5))
            Assert.assertFalse(matcher.matches(10))
            Assert.assertFalse(matcher.matches(15))
            Assert.assertFalse(matcher.matches(42))
        }
        testMatch(NthParser.parse("0n+5"))
        testMatch(NthParser.parse("+5"))
        testMatch(NthParser.parse("5"))
    }

    @Test
    def testFactorOne() {
        def testMatch(matcher: PositionMatcher) {
            Assert.assertNotNull(matcher)
            Assert.assertTrue(matcher.matches(0))
            Assert.assertTrue(matcher.matches(1))
            Assert.assertTrue(matcher.matches(2))
            Assert.assertTrue(matcher.matches(5))
            Assert.assertTrue(matcher.matches(10))
            Assert.assertTrue(matcher.matches(15))
            Assert.assertTrue(matcher.matches(42))
        }
        testMatch(NthParser.parse("1n+7"))
        testMatch(NthParser.parse("1n+0"))
        testMatch(NthParser.parse("1n"))
        testMatch(NthParser.parse("n"))
        testMatch(NthParser.parse("+n"))
        testMatch(NthParser.parse("-n"))
    }

    @Test(expected = classOf[IllegalArgumentException])
    def testIllegal1() {
       NthParser.parse("3 n")
    }

    @Test(expected = classOf[IllegalArgumentException])
    def testIllegal2() {
       NthParser.parse("+ 2n")
    }

    @Test(expected = classOf[IllegalArgumentException])
    def testIllegal3() {
       NthParser.parse("+ 2")
    }

    @Test(expected = classOf[IllegalArgumentException])
    def testIllegal4() {
       NthParser.parse("10n+-1")
    }

    @Test(expected = classOf[IllegalArgumentException])
    def testIllegal5() {
       NthParser.parse("2m+1")
    }

}
