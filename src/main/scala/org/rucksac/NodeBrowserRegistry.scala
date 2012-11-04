package org.rucksac

import collection.mutable


/**
 * @author Oliver Becker
 * @since 26.10.12
 */
object NodeBrowserRegistry {

    private val map: mutable.Map[Class[_], NodeBrowser[_]] = new mutable.HashMap[Class[_], NodeBrowser[_]]()

    map(classOf[org.w3c.dom.Node]) = DomNodeBrowser
    map(classOf[scala.xml.Node]) = XmlNodeBrowser


    def register[T](clazz: Class[T], browser: NodeBrowser[T]) {
        map(clazz) = browser
    }

    def apply[T](n: T): NodeBrowser[T] = {
        val clazz = n.getClass
        map.foreach {entry => if (entry._1.isAssignableFrom(clazz)) return entry._2.asInstanceOf[NodeBrowser[T]] }
        throw new IllegalArgumentException("unsupported node type " + clazz)
    }

}
