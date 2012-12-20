package net.ion.neo;

import java.io.IOException;
import java.util.concurrent.Future;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class ReadSession extends NeoSession<ReadNode>{

	private final Credential credential ;
	private NeoWorkspace wspace ;
	private GraphDatabaseService graphDB ;
	
	ReadSession(Credential credential, NeoWorkspace wspace) {
		this.credential = credential ;
		this.wspace = wspace ;
		this.graphDB = wspace.graphDB();
	}

	public ReadNode rootNode() {
		return ReadNode.findBy(this, graphDB.getNodeById(0));
	}
	
	ReadNode node(Node inner){
		return ReadNode.findBy(this, inner) ;
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

	public SessionQuery<ReadNode> createQuery() {
		return SessionQuery.create(wspace, this) ;
	}

	public ExecutionEngine executionEngine() {
		return new ExecutionEngine(workspace().graphDB());
	}
}
