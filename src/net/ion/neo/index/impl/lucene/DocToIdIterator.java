package net.ion.neo.index.impl.lucene;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.neo4j.graphdb.index.IndexHits;

class DocToIdIterator extends AbstractIndexHits<Long> {
	private final Collection<Long> exclude;
	private IndexReference searcherOrNull;
	private final IndexHits<Document> source;

	DocToIdIterator(IndexHits<Document> source, Collection<Long> exclude, IndexReference searcherOrNull) {
		this.source = source;
		this.exclude = exclude;
		this.searcherOrNull = searcherOrNull;
		if (source.size() == 0) {
			close();
		}
	}

	@Override
	protected Long fetchNextOrNull() {
		Long result = null;
		while (result == null) {
			if (!source.hasNext()) {
				endReached();
				break;
			}
			Document doc = source.next();
			Long id = Long.valueOf(doc.get(LuceneIndex.KEY_DOC_ID));
			if (!exclude.contains(id)) {
				result = id;
			}
		}
		return result;
	}

	protected void endReached() {
		close();
	}

	@Override
	public void close() {
		if (!isClosed()) {
			this.searcherOrNull.close();
			this.searcherOrNull = null;
		}
	}

	public int size() {
		return source.size() - exclude.size();
	}

	private boolean isClosed() {
		return searcherOrNull == null;
	}

	public float currentScore() {
		return source.currentScore();
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
}
