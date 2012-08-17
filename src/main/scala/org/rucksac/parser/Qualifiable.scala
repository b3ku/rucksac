package org.rucksac.parser

protected case class Qualifiable(prefix: String, localName: String) {
    override def toString = (if (prefix != null) prefix + "|" else "*|") + (if (localName != null) localName else "*")
}
