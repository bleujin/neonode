package net.ion.neo.bleujin;

import java.util.Iterator;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleCacheReleaseDBManager;
import net.ion.framework.db.servant.AfterTask;
import net.ion.framework.db.servant.IExtraServant;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class TestViewNeo extends TestCase {
	private final String dbPath = "./resource/db";
	protected GraphDatabaseService graphDB;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
	}

	@Override
	protected void tearDown() throws Exception {
		graphDB.shutdown();
		super.tearDown();
	}
	
	public void testAll() throws Exception {
		Iterator<Node> rels = graphDB.getAllNodes().iterator();
		int count = 0 ;
		while(rels.hasNext()){
			rels.next() ;
			count++ ;
		}
		
		Debug.line(count) ;
	}
	
	public void testView() throws Exception {
		Node root = graphDB.getNodeById(0);
		Node articles = root.getSingleRelationship(TestFromDB.createType("articles"), Direction.INCOMING).getStartNode();
		Iterator<Relationship> rels = articles.getRelationships(Direction.INCOMING).iterator() ;
		int count = 0 ;
		while(rels.hasNext()){
			rels.next() ;
			count++ ;
		}
		Debug.line(count) ;
	}
}
