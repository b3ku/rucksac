package org.rucksac.parser.css

import org.junit.{Assert, Test}

/**
 * @author Oliver Becker
 * @since 18.08.12
 */
class NthParserTest {

    def assertMatch(expression: String, matches: List[Int], doesntMatch: List[Int]) {
        val matcher = NthParser.parse(expression)
        Assert.assertNotNull(matcher)
        for (position <- matches) {
            Assert.assertTrue("must match " + position, matcher.matches(position))
        }
        for (position <- doesntMatch) {
            Assert.assertFalse("must not match " + position, matcher.matches(position))
        }
    }

    def assertMatch(expressions: List[String], matches: List[Int], doesntMatch: List[Int]) {
        for (expression <- expressions) {
            assertMatch(expression, matches, doesntMatch)
        }
    }


    @Test
    def testOdd() {
        assertMatch(List("2n+1", "2N+1", "odd", "ODD"), List(1, 3, 5, 13, 127), List(-1, 0, 2, 4, 6, 12, 42))
    }

    @Test
    def testEven() {
        assertMatch(List("2n+0", "2N", "even", "EVEN"), List(2, 4, 6, 12, 42), List(-1, 0, 1, 3, 5, 13, 127))
    }

    @Test
    def testDefault() {
        assertMatch("4n+2", List(2, 6, 10, 42), List(-1, 0, 1, 3, 4, 5, 7, 8, 9, 13))
    }

    @Test
    def testPlusMinus() {
        assertMatch(List("10n-1", "10n+9", " +10n - 1", " 10n+ 9 "), List(9, 19, 29), List(-1, 0, 1, 8, 10, 18, 20, 42))
    }

    @Test
    def testFactorZero() {
        assertMatch(List("0n+5", "+5", "5"), List(5), List(-1, 0, 1, 2, 3, 4, 6, 10, 15, 42))
    }

    @Test
    def testFactorOne() {
        assertMatch(List("1n+0", "1n", "n", "+n"), List(1, 2, 3, 4, 5, 10, 15, 42), List(-1, 0))
    }

    @Test
    def testFactorOneWithShift() {
        assertMatch(List("1n+3", "+n+3"), List(3, 4, 5, 6, 10, 15, 42), List(-1, 0, 1, 2))
    }

    @Test
    def testFactorMinusOneShift() {
        assertMatch(List("-1n+3", "-n+3"), List(1, 2, 3), List(-1, 0, 4, 5, 6, 10, 15, 42))
    }

    @Test
    def testFactorMinusShift() {
        assertMatch("-2n+3", List(1, 3), List(-1, 0, 2, 4, 5, 6, 7, 10, 15, 42))
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
