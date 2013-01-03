package net.ion.neo;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public abstract class NeoSession<T, R> {

	public abstract T rootNode() ;
	
	abstract T node(Node inner) ;
	
	abstract R relation(Relationship inner) ;

	public abstract NeoWorkspace workspace() ;

}
