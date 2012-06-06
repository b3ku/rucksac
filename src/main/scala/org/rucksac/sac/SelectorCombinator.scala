package org.rucksac.sac

/**
 * @author Oliver Becker
 * @since 05.06.12
 */

abstract sealed class SelectorCombinator

object DescendantCombinator extends SelectorCombinator

object ChildCombinator extends SelectorCombinator

object AdjacentSiblingCombinator extends SelectorCombinator

object GeneralSiblingCombinator extends SelectorCombinator
