package net.ion.neo.index.impl.lucene;

import java.io.IOException;

import net.ion.neo.index.lucene.ValueContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;

public abstract class LuceneUtil {
	static void close(IndexWriter writer) {
		close((Object) writer);
	}

	static void close(IndexSearcher searcher) {
		close((Object) searcher);
	}

	private static void close(Object object) {
		if (object == null) {
			return;
		}

		try {
			if (object instanceof IndexWriter) {
				((IndexWriter) object).close();
			} else if (object instanceof IndexSearcher) {
				((IndexSearcher) object).close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static void strictAddDocument(IndexWriter writer, Document document) {
		try {
			writer.addDocument(document);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static void strictRemoveDocument(IndexWriter writer, Query query) {
		try {
			writer.deleteDocuments(query);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Will create a {@link Query} with a query for numeric ranges, that is values that have been indexed using {@link ValueContext#indexNumeric()}. It will match the type of numbers supplied to the type of values that are indexed in the index, f.ex. long, int, float and double. If both {@code from} and {@code to} is {@code null} then it will default to int.
	 * 
	 * @param key
	 *            the property key to query.
	 * @param from
	 *            the low end of the range (inclusive)
	 * @param to
	 *            the high end of the range (inclusive)
	 * @param includeFrom
	 *            whether or not {@code from} (the lower bound) is inclusive or not.
	 * @param includeTo
	 *            whether or not {@code to} (the higher bound) is inclusive or not.
	 * @return a {@link Query} to do numeric range queries with.
	 */
	public static Query rangeQuery(String key, Number from, Number to, boolean includeFrom, boolean includeTo) {
		if (from instanceof Long || to instanceof Long) {
			return NumericRangeQuery.newLongRange(key, from != null ? from.longValue() : 0, to != null ? to.longValue() : Long.MAX_VALUE, includeFrom, includeTo);
		} else if (from instanceof Double || to instanceof Double) {
			return NumericRangeQuery.newDoubleRange(key, from != null ? from.doubleValue() : 0, to != null ? to.doubleValue() : Double.MAX_VALUE, includeFrom, includeTo);
		} else if (from instanceof Float || to instanceof Float) {
			return NumericRangeQuery.newFloatRange(key, from != null ? from.floatValue() : 0, to != null ? to.floatValue() : Float.MAX_VALUE, includeFrom, includeTo);
		} else {
			return NumericRangeQuery.newIntRange(key, from != null ? from.intValue() : 0, to != null ? to.intValue() : Integer.MAX_VALUE, includeFrom, includeTo);
		}
	}
}
