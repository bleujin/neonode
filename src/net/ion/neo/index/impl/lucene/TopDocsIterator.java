package net.ion.neo.index.impl.lucene;

import java.io.IOException;
import java.util.Iterator;

import net.ion.neo.index.lucene.QueryContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.neo4j.helpers.collection.ArrayIterator;

class TopDocsIterator extends AbstractIndexHits<Document> {
	private final Iterator<ScoreDoc> iterator;
	private ScoreDoc currentDoc;
	private final int size;
	private final IndexSearcher searcher;

	TopDocsIterator(Query query, QueryContext context, IndexSearcher searcher) throws IOException {
		TopDocs docs = toTopDocs(query, context, searcher);
		this.size = docs.scoreDocs.length;
		this.iterator = new ArrayIterator<ScoreDoc>(docs.scoreDocs);
		this.searcher = searcher;
	}

	private TopDocs toTopDocs(Query query, QueryContext context, IndexSearcher searcher) throws IOException {
		Sort sorting = context != null ? context.getSorting() : null;
		TopDocs topDocs = null;
		if (sorting == null) {
			topDocs = searcher.search(query, context.getTop());
		} else {
			boolean forceScore = context == null || !context.getTradeCorrectnessForSpeed();
			if (forceScore) {
				TopFieldCollector collector = LuceneDataSource.scoringCollector(sorting, context.getTop());
				searcher.search(query, collector);
				topDocs = collector.topDocs();
			} else {
				topDocs = searcher.search(query, null, context.getTop(), sorting);
			}
		}
		return topDocs;
	}

	@Override
	protected Document fetchNextOrNull() {
		if (!iterator.hasNext()) {
			return null;
		}
		currentDoc = iterator.next();
		try {
			return searcher.doc(currentDoc.doc);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public float currentScore() {
		return currentDoc.score;
	}

	public int size() {
		return this.size;
	}
}
