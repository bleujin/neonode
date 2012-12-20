package net.ion.neo.index.impl.lucene;

import java.util.Collection;

import net.ion.neo.index.lucene.QueryContext;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

class TxDataHolder {
	final LuceneIndex index;
	private TxData data;

	TxDataHolder(LuceneIndex index, TxData initialData) {
		this.index = index;
		this.data = initialData;
	}

	void add(Object entityId, String key, Object value) {
		this.data.add(this, entityId, key, value);
	}

	void remove(Object entityId, String key, Object value) {
		this.data.remove(this, entityId, key, value);
	}

	Collection<Long> query(Query query, QueryContext contextOrNull) {
		return this.data.query(this, query, contextOrNull);
	}

	Collection<Long> get(String key, Object value) {
		return this.data.get(this, key, value);
	}

	Collection<Long> getOrphans(String key) {
		return this.data.getOrphans(key);
	}

	void close() {
		this.data.close();
	}

	IndexSearcher asSearcher(QueryContext context) {
		return this.data.asSearcher(this, context);
	}

	void set(TxData newData) {
		this.data = newData;
	}
}
