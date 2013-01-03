package net.ion.neo.index.impl.lucene;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexImplementation;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.impl.index.IndexConnectionBroker;

public class LuceneIndexImplementation implements IndexImplementation {
	static final String KEY_TYPE = "type";
	static final String KEY_ANALYZER = "analyzer";
	static final String KEY_TO_LOWER_CASE = "to_lower_case";
	static final String KEY_SIMILARITY = "similarity";
	public static final String SERVICE_NAME = "neolucene";

	public static final Map<String, String> EXACT_CONFIG = Collections.unmodifiableMap(MapUtil.stringMap(IndexManager.PROVIDER, SERVICE_NAME, KEY_TYPE, "exact"));

	public static final Map<String, String> FULLTEXT_CONFIG = Collections.unmodifiableMap(MapUtil.stringMap(IndexManager.PROVIDER, SERVICE_NAME, KEY_TYPE, "fulltext", KEY_TO_LOWER_CASE, "true"));

	public static final int DEFAULT_LAZY_THRESHOLD = 100;

	private final GraphDatabaseService graphDb;
	private IndexConnectionBroker<LuceneXaConnection> broker;
	private LuceneDataSource dataSource;
	final int lazynessThreshold;

	public LuceneIndexImplementation(GraphDatabaseService db, LuceneDataSource dataSource, IndexConnectionBroker<LuceneXaConnection> broker) {
		this.graphDb = db;
		this.dataSource = dataSource;
		this.broker = broker;
		this.lazynessThreshold = DEFAULT_LAZY_THRESHOLD;
	}

	IndexConnectionBroker<LuceneXaConnection> broker() {
		return broker;
	}

	LuceneDataSource dataSource() {
		return dataSource;
	}

	@Override
	public Index<Node> nodeIndex(String indexName, Map<String, String> config) {
		return dataSource.nodeIndex(indexName, graphDb, this);
	}

	@Override
	public RelationshipIndex relationshipIndex(String indexName, Map<String, String> config) {
		return dataSource.relationshipIndex(indexName, graphDb, this);
	}

	@Override
	public Map<String, String> fillInDefaults(Map<String, String> source) {
		Map<String, String> result = source != null ? new HashMap<String, String>(source) : new HashMap<String, String>();
		String analyzer = result.get(KEY_ANALYZER);
		if (analyzer == null) {
			// Type is only considered if "analyzer" isn't supplied
			String type = result.get(KEY_TYPE);
			if (type == null) {
				type = "exact";
				result.put(KEY_TYPE, type);
			}
			if (type.equals("fulltext")) {
				if (!result.containsKey(LuceneIndexImplementation.KEY_TO_LOWER_CASE)) {
					result.put(KEY_TO_LOWER_CASE, "true");
				}
			}
		}
		return result;
	}

	@Override
	public boolean configMatches(Map<String, String> storedConfig, Map<String, String> config) {
		return match(storedConfig, config, KEY_TYPE, null) && match(storedConfig, config, KEY_TO_LOWER_CASE, "true") && match(storedConfig, config, KEY_ANALYZER, null) && match(storedConfig, config, KEY_SIMILARITY, null);
	}

	private boolean match(Map<String, String> storedConfig, Map<String, String> config, String key, String defaultValue) {
		String value1 = storedConfig.get(key);
		String value2 = config.get(key);
		if (value1 == null || value2 == null) {
			if (value1 == value2) {
				return true;
			}
			if (defaultValue != null) {
				value1 = value1 != null ? value1 : defaultValue;
				value2 = value2 != null ? value2 : defaultValue;
				return value1.equals(value2);
			}
		} else {
			return value1.equals(value2);
		}
		return false;
	}

	@Override
	public String getDataSourceName() {
		return LuceneDataSource.DEFAULT_NAME;
	}

	public boolean matches(GraphDatabaseService gdb) {
		return this.graphDb.equals(gdb);
	}

	public void reset(LuceneDataSource dataSource, IndexConnectionBroker<LuceneXaConnection> broker) {
		this.broker = broker;
		this.dataSource = dataSource;
	}

	public GraphDatabaseService graphDb() {
		return graphDb;
	}
}
