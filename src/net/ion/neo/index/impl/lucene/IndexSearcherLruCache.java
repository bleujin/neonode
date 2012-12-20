package net.ion.neo.index.impl.lucene;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.neo4j.helpers.Pair;
import org.neo4j.kernel.impl.cache.LruCache;

/**
 * An Lru Cache for Lucene Index searchers.
 * 
 * @see LuceneDataSource
 */
public class IndexSearcherLruCache extends LruCache<IndexIdentifier, Pair<IndexReference, AtomicBoolean>> {
	/**
	 * Creates a LRU cache. If <CODE>maxSize < 1</CODE> an IllegalArgumentException is thrown.
	 * 
	 * @param maxSize
	 *            maximum size of this cache
	 */
	public IndexSearcherLruCache(int maxSize) {
		super("IndexSearcherCache", maxSize);
	}

	@Override
	public void elementCleaned(Pair<IndexReference, AtomicBoolean> searcher) {
		try {
			searcher.first().dispose(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}