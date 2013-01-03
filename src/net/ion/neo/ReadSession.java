package net.ion.neo;

import java.io.IOException;
import java.util.concurrent.Future;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class ReadSession extends NeoSession<ReadNode, ReadRelationship>{

	private final Credential credential ;
	private NeoWorkspace wspace ;
	
	ReadSession(Credential credential, NeoWorkspace wspace) {
		this.credential = credential ;
		this.wspace = wspace ;
	}

	public ReadNode rootNode() {
		return node(wspace.getNodeById(0L));
	}
	
	ReadNode node(Node inner){
		return ReadNode.load(this, inner) ;
	}

	ReadRelationship relation(Relationship inner){
		return ReadRelationship.load(this, inner) ;
	}

	public NeoWorkspace workspace() {
		return wspace ;
	}
	
	public Credential credential(){
		return credential ; 
	}

	public void dropWorkspace() throws IOException {
		wspace.clear() ;
	}

	public <F> Future<F> tran(TransactionJob<F> tjob) {
		return wspace.tran(this, tjob, TranExceptionHandler.PRINT) ;
	}

	public SessionQuery<ReadNode, ReadRelationship> createQuery() {
		return SessionQuery.create(this) ;
	}

	public ExecutionEngine executionEngine() {
		return wspace.executionEngine() ;
	}

	public RelationQuery<ReadRelationship> relationshipQuery() {
		return RelationQuery.create(this) ;
	}

	public ReadNode findById(long id) {
		return node(wspace.getNodeById(id));
	}
}
