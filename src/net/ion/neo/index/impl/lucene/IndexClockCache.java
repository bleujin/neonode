package net.ion.neo.index.impl.lucene;

import java.io.IOException;

import org.neo4j.kernel.impl.cache.ClockCache;

public class IndexClockCache extends ClockCache<IndexIdentifier, IndexReference> {
	public IndexClockCache(int maxSize) {
		super("IndexSearcherCache", maxSize);
	}

	@Override
	public void elementCleaned(IndexReference searcher) {
		try {
			searcher.dispose(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}