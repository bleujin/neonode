package net.ion.neo;

import org.neo4j.graphdb.Path;

public class NeoPath {

	private ReadSession session ;
	private Path path ;
	private NeoPath(ReadSession session, Path path) {
		this.session = session ;
		this.path = path ;
	}

	public static NeoPath load(ReadSession rsession, Path path) {
		return new NeoPath(rsession, path);
	}

	public ReadNode staratNode(){
		return session.node(path.startNode()) ;
	}

	public ReadNode endNode(){
		return session.node(path.endNode()) ;
	}
	
	public int length(){
		return path.length() ;
	}

	public Iterable<ReadNode> nodes(){
		return new IterableReadNode(session, path.nodes()) ;
	}

	public Iterable<ReadRelationship> relationships(){
		return new IterableReadRelation(session, path.relationships()) ;
	}

	
	
}
