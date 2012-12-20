package net.ion.neo.index.impl.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;

public class HitsIterator extends AbstractIndexHits<Document> {
	private final int size;
	private final Hits hits;
	private int index;

	public HitsIterator(Hits hits) {
		this.size = hits.length();
		this.hits = hits;
	}

	@Override
	protected Document fetchNextOrNull() {
		int i = index++;
		try {
			return i < size() ? hits.doc(i) : null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public float currentScore() {
		int i = index - 1;
		try {
			return i >= 0 && i < size() ? hits.score(i) : -1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int size() {
		return this.size;
	}
}
