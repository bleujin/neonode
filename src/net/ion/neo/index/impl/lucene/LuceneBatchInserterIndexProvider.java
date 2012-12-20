package net.ion.neo.index.impl.lucene;

import java.util.Map;

import org.neo4j.graphdb.index.BatchInserterIndex;
import org.neo4j.graphdb.index.BatchInserterIndexProvider;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;

/**
 * The {@link BatchInserter} version of {@link LuceneIndexImplementation}. Indexes created and populated using {@link BatchInserterIndex}s from this provider are compatible with {@link Index}s from {@link LuceneIndexImplementation}.
 * 
 * @deprecated This class has been replaced by {@link net.ion.neo.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider} as of Neo4j 1.7.
 */
public class LuceneBatchInserterIndexProvider implements BatchInserterIndexProvider {
	private final LuceneBatchInserterIndexProviderImpl provider;

	/**
	 * @deprecated This class has been replaced by {@link net.ion.neo.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider} as of Neo4j 1.7.
	 */
	public LuceneBatchInserterIndexProvider(final BatchInserter inserter) {
		provider = new LuceneBatchInserterIndexProviderImpl(inserter);
	}

	public BatchInserterIndex nodeIndex(String indexName, Map<String, String> config) {
		return provider.nodeIndex(indexName, config);
	}

	public BatchInserterIndex relationshipIndex(String indexName, Map<String, String> config) {
		return provider.relationshipIndex(indexName, config);
	}

	public void shutdown() {
		provider.shutdown();
	}
}
