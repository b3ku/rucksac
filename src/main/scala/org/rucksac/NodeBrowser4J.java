package org.rucksac;

import java.util.List;

import scala.collection.Seq;

/**
 * Abstract support class for Java implementations of {@link NodeBrowser} that provides the conversion between Java's
 * {@link List} and Scala's {@link Seq} collection types for the {@link #children(Object)} method
 *
 * @author Oliver Becker
 * @since 04.11.12
 */
public abstract class NodeBrowser4J<T> implements NodeBrowser<T> {

    @Override
    final public Seq<T> children(T node) {
        return scala.collection.JavaConversions.asScalaBuffer(getChildren(node)).toList();
    }

    public abstract List<T> getChildren(T node);

}
