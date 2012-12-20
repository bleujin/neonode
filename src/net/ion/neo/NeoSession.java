package net.ion.neo;

import org.neo4j.graphdb.Node;

public abstract class NeoSession<T> {

	abstract T node(Node inner) ;

	public abstract NeoWorkspace workspace() ;

}
