package net.ion.neo.index.impl.lucene;

import java.util.Collection;

import net.ion.neo.index.lucene.QueryContext;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

abstract class TxData {
	final LuceneIndex index;

	TxData(LuceneIndex index) {
		this.index = index;
	}

	abstract void add(TxDataHolder holder, Object entityId, String key, Object value);

	abstract void remove(TxDataHolder holder, Object entityId, String key, Object value);

	abstract Collection<Long> query(TxDataHolder holder, Query query, QueryContext contextOrNull);

	abstract Collection<Long> get(TxDataHolder holder, String key, Object value);

	abstract Collection<Long> getOrphans(String key);

	abstract void close();

	abstract IndexSearcher asSearcher(TxDataHolder holder, QueryContext context);
}
