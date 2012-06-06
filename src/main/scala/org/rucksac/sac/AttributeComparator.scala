package org.rucksac.sac

/**
 * @author Oliver Becker
 * @since 05.06.12
 */

abstract sealed class AttributeComparator

object AttrEqualsComparator extends AttributeComparator

object AttrHatComparator extends AttributeComparator

object AttrDollarComparator extends AttributeComparator

object AttrTildeComparator extends AttributeComparator

object AttrBarComparator extends AttributeComparator

object AttrStarComparator extends AttributeComparator
