package net.ion.neo.bleujin;

import java.io.File;
import java.io.IOException;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import junit.framework.TestCase;

public class TestBase extends TestCase {
	protected static enum RelTypes implements RelationshipType {
		KNOWS
	}

	private final String dbPath = "./resource/db";
	protected GraphDatabaseService graphDB;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		clearDb();

		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdonwHook(graphDB);
	}

	@Override
	protected void tearDown() throws Exception {
//		registerShutdonwHook(graphDB);
		super.tearDown();
	}

	protected GraphDatabaseService getGraphService() {
		return graphDB;
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

	protected void createSampleNode() {
		Transaction tran = graphDB.beginTx();
		

//		graphDB.getNodeById(0).setProperty("name", "root") ;
		
		try {
			Node bleujin = graphDB.createNode();
			bleujin.setProperty("name", "bleujin") ;
			
			Node hero = graphDB.createNode() ;
			hero.setProperty("name", "bleujin") ;
			
			
			Relationship rel = bleujin.createRelationshipTo(hero, RelTypes.KNOWS);
			rel.setProperty("type", "duplganger");
			tran.success() ;
		} finally {
			tran.finish() ;
		}
	}

}
