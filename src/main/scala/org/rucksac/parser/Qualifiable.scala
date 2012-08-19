package org.rucksac.parser

//TODO really support namespaces!
protected case class Qualifiable(uri: String, localName: String) {
    override def toString = (if (uri != null) uri + "|" else "*|") + (if (localName != null) localName else "*")
}
