package net.ion.neo.bleujin;

import java.io.File;
import java.util.Iterator;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.FileUtil;
import net.ion.neo.bleujin.TestBase.RelTypes;

import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class TestFindByRelation extends TestCase {

	private final String dbPath = "./resource/db";
	protected GraphDatabaseService graphDB;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		FileUtil.deleteDirectory(new File(dbPath));
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
	}

	@Override
	protected void tearDown() throws Exception {
		graphDB.shutdown() ;
		super.tearDown();
	}
	
	public void testFindByRelationShip() throws Exception {
		Transaction tx = graphDB.beginTx();

		Node root = graphDB.getNodeById(0);
		if (! root.hasRelationship(Direction.INCOMING, new PARENT())){
			Node bleujin = graphDB.createNode();
			bleujin.createRelationshipTo(root, new PARENT()).setProperty("id", "bleujin") ;
			bleujin.setProperty("id", "bleujin") ;

			Node hero = graphDB.createNode();
			hero.createRelationshipTo(bleujin, new PARENT()).setProperty("id", "hero") ;
			hero.setProperty("id", "hero") ;
		} ;

		tx.success() ;
		tx.finish() ;
		
		traverse(root) ;
		
	}
	
	private void traverse(Node parent) {
		Iterator<Relationship> rels = parent.getRelationships(Direction.INCOMING).iterator();
		while(rels.hasNext()){
			Relationship rel = rels.next();
			Node startNode = rel.getStartNode();
			Debug.line(startNode, startNode.getProperty("id")) ;
			traverse(startNode) ;
		}
	}

	private static class PARENT implements RelationshipType {
		@Override
		public String name() {
			return "parent";
		}
		
	}
	
	public void xtestFirst() throws Exception {
		Transaction tx = graphDB.beginTx();
		
		Node firstNode = graphDB.createNode();
		firstNode.setProperty("message", "Hello, ") ;
		Node secondNode = graphDB.createNode();
		secondNode.setProperty("message", "World!") ;
		
		Relationship relationShip = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
		relationShip.setProperty("message", "brave Neo4j") ;
		tx.success() ;
		tx.finish() ;
		
		Debug.line(firstNode.getId(), secondNode.getId(), firstNode.getProperty("message")) ;
	}
	

	
}
