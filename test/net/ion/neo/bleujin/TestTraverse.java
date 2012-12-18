package net.ion.neo.bleujin;

import net.ion.framework.util.Debug;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;

public class TestTraverse extends TestBase {

	public enum RelTypes implements RelationshipType {
		NEO_NODE, KNOWS, CODED_BY
	}

	private long matrixNodeId;

	public void testPrintNeoFriends() {
		createNodespace() ;
		
		Node neoNode = getNeoNode();
		// START SNIPPET: friends-usage
		int numberOfFriends = 0;
		String output = neoNode.getProperty("name") + "'s friends:\n";
		TraversalDescription td = Traversal.description().breadthFirst().relationships(RelTypes.KNOWS, Direction.OUTGOING).evaluator(Evaluators.all());
		Traverser friendsTraverser = td.traverse(neoNode);

		for (Path friendPath : friendsTraverser) {
			output += "At depth " + friendPath.length() + " => " + friendPath.endNode().getProperty("name") + "\t"+ "\n";
			numberOfFriends++;
		}
		output += "Number of friends found: " + numberOfFriends + "\n";
		// END SNIPPET: friends-usage
		Debug.line(output);
	}

	private Node getNeoNode() {
		return graphDB.getNodeById(matrixNodeId).getSingleRelationship(RelTypes.NEO_NODE, Direction.OUTGOING).getEndNode();
	}


	public void createNodespace() {
		Transaction tx = graphDB.beginTx();
		try {
			// Create matrix node
			Node matrix = graphDB.createNode();
			matrixNodeId = matrix.getId();

			// Create Neo
			Node thomas = graphDB.createNode();
			thomas.setProperty("name", "Thomas Anderson");
			thomas.setProperty("age", 29);
			// connect Neo/Thomas to the reference node
			matrix.createRelationshipTo(thomas, RelTypes.NEO_NODE);

			Node trinity = graphDB.createNode();
			trinity.setProperty("name", "Trinity");
			Relationship rel = thomas.createRelationshipTo(trinity, RelTypes.KNOWS);
			rel.setProperty("age", "3 days");

			Node morpheus = graphDB.createNode();
			morpheus.setProperty("name", "Morpheus");
			morpheus.setProperty("rank", "Captain");
			morpheus.setProperty("occupation", "Total badass");
			thomas.createRelationshipTo(morpheus, RelTypes.KNOWS);

			rel = morpheus.createRelationshipTo(trinity, RelTypes.KNOWS);
			rel.setProperty("age", "12 years");

			Node cypher = graphDB.createNode();
			cypher.setProperty("name", "Cypher");
			cypher.setProperty("last name", "Reagan");
			trinity.createRelationshipTo(cypher, RelTypes.KNOWS);
			rel = morpheus.createRelationshipTo(cypher, RelTypes.KNOWS);
			rel.setProperty("disclosure", "public");
			Node smith = graphDB.createNode();
			smith.setProperty("name", "Agent Smith");
			smith.setProperty("version", "1.0b");
			smith.setProperty("language", "C++");
			rel = cypher.createRelationshipTo(smith, RelTypes.KNOWS);
			rel.setProperty("disclosure", "secret");
			rel.setProperty("age", "6 months");
			Node architect = graphDB.createNode();
			architect.setProperty("name", "The Architect");
			smith.createRelationshipTo(architect, RelTypes.CODED_BY);

			tx.success();
		} finally {
			tx.finish();
		}
	}
}
