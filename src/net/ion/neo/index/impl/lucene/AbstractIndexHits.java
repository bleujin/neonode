package net.ion.neo.index.impl.lucene;

import java.util.Iterator;

import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.helpers.collection.PrefetchingIterator;

//TODO this is generic and should move out of the Lucene - component
public abstract class AbstractIndexHits<T> extends PrefetchingIterator<T> implements IndexHits<T> {
	public Iterator<T> iterator() {
		return this;
	}

	public void close() {
	}

	public T getSingle() {
		try {
			return IteratorUtil.singleOrNull((Iterator<T>) this);
		} finally {
			close();
		}
	}
}
