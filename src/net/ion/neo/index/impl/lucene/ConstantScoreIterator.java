package net.ion.neo.index.impl.lucene;

import java.util.Collection;
import java.util.Iterator;

class ConstantScoreIterator<T> extends AbstractIndexHits<T> {
	private final Iterator<T> items;
	private final int size;
	private final float score;

	ConstantScoreIterator(Collection<T> items, float score) {
		this.items = items.iterator();
		this.score = score;
		this.size = items.size();
	}

	public float currentScore() {
		return this.score;
	}

	public int size() {
		return this.size;
	}

	@Override
	protected T fetchNextOrNull() {
		return items.hasNext() ? items.next() : null;
	}
}
