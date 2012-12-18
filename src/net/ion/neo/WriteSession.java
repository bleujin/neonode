package net.ion.neo;

import java.util.Iterator;

import net.ion.framework.util.Debug;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;

public class WriteSession {

	private NeoSession session ;
	private NeoWorkspace workspace ;
	private GraphDatabaseService graphDB ;
	
	WriteSession(NeoSession session, NeoWorkspace workspace, GraphDatabaseService graphDB) {
		this.session = session ;
		this.workspace = workspace ; 
		this.graphDB = graphDB ;
	}
	
	WriteNode mergeChildNode(WriteNode parent, String relName) {
		
		Iterator<WriteRelationship> rels = parent.relationShips(Direction.OUTGOING, RelType.CHILD).iterator() ;
		
		while(rels.hasNext()){
			WriteRelationship relationShip = rels.next();
			if (relName.equals(relationShip.property(NeoConstant.ChildRelationName))){
				return relationShip.endNode() ;
			}
		}
		
		WriteNode result = newNode() ;
		WriteRelationship relationShip = parent.createRelationshipTo(result, RelType.CHILD);
		relationShip.property(NeoConstant.ChildRelationName, relName) ;
			
		return result ;
	}
	
	public WriteNode rootNode() {
		return WriteNode.findBy(this, graphDB.getNodeById(0)) ;
	}
	
	
	public WriteNode newNode(){
		Node newNode = graphDB.createNode() ;
		return WriteNode.findBy(this, newNode) ;
	}
	
	NeoWorkspace workspace() {
		return workspace;
	}
	
	

}
