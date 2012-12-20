package net.ion.neo.index.lucene.unsafe.batchinsert;

import java.util.Map;

import net.ion.neo.index.impl.lucene.LuceneBatchInserterIndexProviderNewImpl;

import org.neo4j.graphdb.index.Index;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;

/**
 * The {@link org.neo4j.unsafe.batchinsert.BatchInserter} version of the Lucene-based indexes. Indexes created and populated using {@link org.neo4j.unsafe.batchinsert.BatchInserterIndex}s from this provider are compatible with the normal {@link Index}es.
 */
public class LuceneBatchInserterIndexProvider implements BatchInserterIndexProvider {
	private final BatchInserterIndexProvider provider;

	public LuceneBatchInserterIndexProvider(final BatchInserter inserter) {
		provider = new LuceneBatchInserterIndexProviderNewImpl(inserter);
	}

	@Override
	public BatchInserterIndex nodeIndex(String indexName, Map<String, String> config) {
		return provider.nodeIndex(indexName, config);
	}

	@Override
	public BatchInserterIndex relationshipIndex(String indexName, Map<String, String> config) {
		return provider.relationshipIndex(indexName, config);
	}

	@Override
	public void shutdown() {
		provider.shutdown();
	}
}
