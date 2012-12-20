package net.ion.neo.bleujin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.kernel.impl.util.FileUtils;
import org.neo4j.tooling.GlobalGraphOperations;

public class TestIndex extends TestCase {
	protected static enum RelTypes implements RelationshipType {
		KNOWS
	}

	private final String dbPath = "./resource/db";
	protected GraphDatabaseService graphDB;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		clearDb() ;

		this.graphDB = newImperDB() ;
		registerShutdonwHook(graphDB);
	}

	private GraphDatabaseService newImperDB() throws IOException {
		Map<String, String> config = MapUtil.newMap() ;
        config.put( "neostore.nodestore.db.mapped_memory", "10M" );
        config.put( "string_block_size", "60" );
        config.put( "array_block_size", "300" );
        
//        GraphDatabaseService db = new ImpermanentGraphDatabase( config );
        GraphDatabaseService db = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
        return db ;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAutoIndex() throws Exception {
		final IndexManager im = graphDB.index();

		final AutoIndexer<Node> aindexer = im.getNodeAutoIndexer();
		aindexer.startAutoIndexingProperty("name") ;
		aindexer.startAutoIndexingProperty("age") ;
		aindexer.setEnabled(true) ;
		

		createSampleNode() ;

		
		IndexHits<Node> hits = aindexer.getAutoIndex().get("age", 20);
		for (Node found : hits) {
			Debug.debug(found, found.getPropertyValues()) ;
		}
		hits.close() ;
		
		Query query = new QueryParser(Version.LUCENE_35, "", new StandardAnalyzer(Version.LUCENE_35)).parse("age:[25 TO 40]");
		
		hits = aindexer.getAutoIndex().query(query) ;
		for (Node found : hits) {
			Debug.debug(found, found.getPropertyValues()) ;
		}
		hits.close() ;
		
	}
	
	
	public void testFoundByNameIndex() throws Exception {
		final IndexManager im = graphDB.index();
		createSampleNode() ;
		
		Debug.line(im.getNodeAutoIndexer().isEnabled(), im.getNodeAutoIndexer().getAutoIndexedProperties()) ;
		
		Transaction tran = graphDB.beginTx();
		Index<Node> nodeIndex = im.forNodes("nodes");
		for(Node node : GlobalGraphOperations.at(graphDB).getAllNodes()){
			if (! node.hasProperty("name")) continue ;
			nodeIndex.add(node, "name", node.getProperty("name")) ;
		}
		tran.success() ;
		tran.finish() ;
		IndexHits<Node> hits = nodeIndex.get("name", "bleujin");
		for (Node found : hits) {
			Debug.line(found) ;
		}
	}
	
	protected void createSampleNode() {
		Transaction tran = graphDB.beginTx();

//		graphDB.getNodeById(0).setProperty("name", "root") ;
		
		try {
			Node bleujin = graphDB.createNode();
			bleujin.setProperty("name", "bleujin") ;
			bleujin.setProperty("age", 20) ;
			
			Node hero = graphDB.createNode() ;
			hero.setProperty("name", "hero") ;
			hero.setProperty("age", 33) ;
			
			
			Relationship rel = bleujin.createRelationshipTo(hero, RelTypes.KNOWS);
			rel.setProperty("type", "duplganger");
			tran.success() ;
		} finally {
			tran.finish() ;
		}
	}
	
	
	private void clearDb() {
		try {
			FileUtils.deleteRecursively(new File(dbPath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void registerShutdonwHook(final GraphDatabaseService graphDB) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (graphDB != null)
					graphDB.shutdown();
			}
		});
	}
}
