package net.ion.neo.index.impl.lucene;

import java.util.Collection;
import java.util.Iterator;

import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.CombiningIterator;
import org.neo4j.helpers.collection.IteratorUtil;

public class CombinedIndexHits<T> extends CombiningIterator<T> implements IndexHits<T> {
	private final Collection<IndexHits<T>> allIndexHits;
	private final int size;

	public CombinedIndexHits(Collection<IndexHits<T>> iterators) {
		super(iterators);
		this.allIndexHits = iterators;
		size = accumulatedSize(iterators);
	}

	private int accumulatedSize(Collection<IndexHits<T>> iterators) {
		int result = 0;
		for (IndexHits<T> hits : iterators) {
			result += hits.size();
		}
		return result;
	}

	public Iterator<T> iterator() {
		return this;
	}

	@Override
	protected IndexHits<T> currentIterator() {
		return (IndexHits<T>) super.currentIterator();
	}

	public int size() {
		return size;
	}

	public void close() {
		for (IndexHits<T> hits : allIndexHits) {
			hits.close();
		}
		allIndexHits.clear();
	}

	public T getSingle() {
		try {
			return IteratorUtil.singleOrNull((Iterator<T>) this);
		} finally {
			close();
		}
	}

	public float currentScore() {
		return currentIterator().currentScore();
	}
}
