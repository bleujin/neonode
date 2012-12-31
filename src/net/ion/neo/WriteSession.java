package net.ion.neo;

import java.util.Iterator;

import net.ion.framework.util.Debug;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;

public class WriteSession extends NeoSession<WriteNode, WriteRelationship> {

	private ReadSession session ;
	private NeoWorkspace wspace ;
	
	WriteSession(ReadSession session, NeoWorkspace workspace) {
		this.session = session ;
		this.wspace = workspace ; 
	}
	
	WriteNode mergeRelationNode(WriteNode parent, RelationshipType rtype, String relName) {
		
		Iterator<WriteRelationship> rels = parent.relationShips(Direction.OUTGOING, rtype).iterator() ;
		
		while(rels.hasNext()){
			WriteRelationship relationShip = rels.next();
			if (relName.equals(relationShip.property(NeoConstant.RelationName))){
				return relationShip.endNode() ;
			}
		}
		
		WriteNode result = newNode() ;
		WriteRelationship relationShip = parent.createRelationshipTo(result, rtype, relName);
			
		return result ;
	}

	WriteNode createRelationNode(WriteNode parent, RelationshipType rtype, String relName) {
		WriteNode result = newNode() ;
		WriteRelationship relationShip = parent.createRelationshipTo(result, rtype, relName);
		return result ;
	}

	public WriteNode rootNode() {
		return node(workspace().getNodeById(0)) ;
	}
	
	WriteNode node(Node inner){
		return WriteNode.load(this, inner) ;
	}
	
	WriteRelationship relation(Relationship inner){
		return WriteRelationship.load(this, inner) ;
	}
	
	public WriteNode newNode(){
		return node(workspace().createNode()) ;
	}
	
	public NeoWorkspace workspace() {
		return wspace;
	}

	public SessionQuery<WriteNode> createQuery() {
		return SessionQuery.create(this) ;
	}


	public ExecutionEngine executionEngine() {
		return wspace.executionEngine() ;
	}

	public RelationQuery<WriteRelationship> relationshipQuery() {
		return RelationQuery.create(this) ;
	}

}
