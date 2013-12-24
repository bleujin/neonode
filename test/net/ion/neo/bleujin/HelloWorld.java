package net.ion.neo.bleujin;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.tooling.GlobalGraphOperations;

public class HelloWorld extends TestBase {

	public void testFirst() throws Exception {

		Transaction tx = graphDB.beginTx();
		
		Node firstNode = graphDB.createNode();
		firstNode.setProperty("message", "Hello, ") ;
		Node secondNode = graphDB.createNode();
		secondNode.setProperty("message", "World!") ;
		
		Relationship relationShip = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
		relationShip.setProperty("message", "brave Neo4j") ;
		tx.success() ;
		tx.finish() ;
	}
	
	public void testCreateDB() throws Exception {

		Transaction tx = graphDB.beginTx();
		try {
			Node firstNode = graphDB.createNode();
			
			firstNode.setProperty("message", "Hello, ");
			firstNode.setProperty("int", 33);
			firstNode.setProperty("long", 33L);
			Node secondNode = graphDB.createNode();
			secondNode.setProperty("message", " World!");

			
			
			Relationship rel = firstNode.createRelationshipTo(firstNode, RelTypes.KNOWS);
			rel.setProperty("message", "brave Neo");

			String greeting = ((String) firstNode.getProperty("message")) + ((String) rel.getProperty("message")) + ((String) secondNode.getProperty("message"));
			Debug.line(greeting) ;
			Debug.line(firstNode.getId()) ;
			
			Node found = graphDB.getNodeById(firstNode.getId());
			Debug.line(found.getPropertyKeys(), found.getRelationships(), found.getPropertyValues()) ;
			
		} finally {
			tx.finish();
		}
	}
	
	public void testRelation() throws Exception {
		super.createSampleNode() ;
		
		Iterable<Relationship> rels = graphDB.getNodeById(1).getRelationships(Direction.BOTH);
		for (Relationship rel : rels) {
			Debug.line(rel, rel.getStartNode(), rel.getEndNode()) ;
		}
		
	}
	
	
	
	
	public void testFoundByName() throws Exception {
		super.createSampleNode() ;
		Index<Node> nodeIndex = graphDB.index().forNodes("nodes");
		
		Transaction tran = graphDB.beginTx();
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

}

class Debug {

	public static void line(Object... found) {
		System.out.println("===" + found) ;
	}
	
}
