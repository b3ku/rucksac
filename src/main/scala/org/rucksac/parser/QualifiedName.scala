package org.rucksac.parser

/**
 * @author Andreas Kuhrwahl
 * @since 01.09.12
 */
case class QualifiedName(uri: String, name: String) {
    override def toString = Option(uri).getOrElse("*") + '|' + Option(name).getOrElse("*")
}
