package net.ion.neo.index.impl.lucene;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.helpers.Pair;
import org.neo4j.kernel.impl.index.IndexStore;

class IndexTypeCache {
	private final Map<IndexIdentifier, Pair<Integer, IndexType>> cache = Collections.synchronizedMap(new HashMap<IndexIdentifier, Pair<Integer, IndexType>>());
	private final IndexStore indexStore;

	IndexTypeCache(IndexStore indexStore) {
		this.indexStore = indexStore;
	}

	IndexType getIndexType(IndexIdentifier identifier, boolean recovery) {
		Pair<Integer, IndexType> type = cache.get(identifier);
		Map<String, String> config = indexStore.get(identifier.entityType.getType(), identifier.indexName);
		if (type != null && config.hashCode() == type.first()) {
			return type.other();
		}
		if (config == null) {
			if (recovery)
				return null;
			throw new IllegalArgumentException("Unknown index " + identifier);
		}
		type = Pair.of(config.hashCode(), IndexType.getIndexType(identifier, config));
		cache.put(identifier, type);
		return type.other();
	}

	void invalidate(IndexIdentifier identifier) {
		cache.remove(identifier);
	}
}
