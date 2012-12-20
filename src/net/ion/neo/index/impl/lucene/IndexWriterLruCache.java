package net.ion.neo.index.impl.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.neo4j.kernel.impl.cache.LruCache;

/**
 * An Lru Cache for Lucene Index Writers.
 * 
 * @see LuceneDataSource
 */
public class IndexWriterLruCache extends LruCache<IndexIdentifier, IndexWriter> {
	/**
	 * Creates a LRU cache. If <CODE>maxSize < 1</CODE> an IllegalArgumentException is thrown.
	 * 
	 * @param maxSize
	 *            maximum size of this cache
	 */
	public IndexWriterLruCache(int maxSize) {
		super("IndexWriterCache", maxSize);
	}

	@Override
	public void elementCleaned(IndexWriter writer) {
		try {
			writer.close(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
