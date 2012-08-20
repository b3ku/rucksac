package org.rucksac.parser

import org.rucksac.ParseException

/**
 * Parser for nth grammar in CSS 3 selectors (for :nth-child(...) etc)
 *
 * @see http://www.w3.org/TR/selectors/#nth-child-pseudo
 * @author Oliver Becker
 * @since 18.08.12
 */
object NthParser {

    private val regex = """(?i)\s*((([-+]?)(\d*))n\s*(([-+])\s*(\d+))?|(([-+]?)(\d+))|(odd)|(even))\s*""".r

    //    private val factorGroup = 2
    private val factorSignGroup        = 3
    private val factorNumberGroup      = 4
    //    private val shiftGroup = 5
    private val shiftSignGroup         = 6
    private val shiftNumberGroup       = 7
    private val singleShiftGroup       = 8
    private val singleShiftSignGroup   = 9
    private val singleShiftNumberGroup = 10
    private val oddGroup               = 11
    private val evenGroup              = 12

    def parse(s: String): PositionMatcher = {

        val matcher = regex.pattern.matcher(s)

        def matches(group: Int) = matcher.group(group) != null

        if (matcher.matches()) {
            if (matches(evenGroup)) {
                EvenPositionMatcher
            } else if (matches(oddGroup)) {
                OddPositionMatcher
            } else if (matches(singleShiftGroup)) {
                PositionMatcher.create("", "0",
                    matcher.group(singleShiftSignGroup), matcher.group(singleShiftNumberGroup))
            } else {
                PositionMatcher.create(
                    matcher.group(factorSignGroup), matcher.group(factorNumberGroup),
                    matcher.group(shiftSignGroup), matcher.group(shiftNumberGroup))
            }
        } else {
            throw new ParseException(s)
        }

    }

}

case class PositionMatcher(factor: Int, shift: Int) {

    def matches(position: Int) = {
        val v = position - shift
        if (position < 1) {
            false
        } else if (factor == 0) {
            v == 0
        } else {
            v % factor == 0 && v / factor >= 0
        }
    }

}

object EvenPositionMatcher extends PositionMatcher(2, 0)

object OddPositionMatcher extends PositionMatcher(2, 1)

object PositionMatcher {

    def create(factorSign: String, factorNumber: String, shiftSign: String, shiftNumber: String) = {
        var factor = 1
        if (factorNumber != null && !factorNumber.isEmpty) {
            factor = factorNumber.toInt
        }
        if ("-" == factorSign) {
            factor = -factor
        }
        var shift = 0
        if (shiftNumber != null && !shiftNumber.isEmpty) {
            shift = shiftNumber.toInt
        }
        if ("-" == shiftSign) {
            shift = -shift
        }
        new PositionMatcher(factor, shift)
    }

}
