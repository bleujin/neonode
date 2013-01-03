package net.ion.neo;

import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Path;

public abstract class NeoPath<T, R> {

	private Path path ;
	protected NeoPath(Path path) {
		this.path = path ;
	}

	public int length(){
		return path.length() ;
	}

	public abstract T startNode() ;
	public abstract T endNode() ;
	public abstract ListIterable<T> nodes() ;
	

	public abstract ListIterable<R> relationships() ;
	
	
}

class ReadNeoPath extends NeoPath<ReadNode, ReadRelationship>{

	private ReadSession session ;
	private Path path ;
	private ReadNeoPath(ReadSession session, Path path) {
		super(path) ;
		this.session = session ;
		this.path = path ;
	}

	public static ReadNeoPath load(ReadSession rsession, Path path) {
		return new ReadNeoPath(rsession, path);
	}

	public int length(){
		return path.length() ;
	}

	public ReadNode startNode(){
		return session.node(path.startNode()) ;
	}

	public ReadNode endNode(){
		return session.node(path.endNode()) ;
	}
	
	public ListIterable<ReadNode> nodes(){
		return new IterableReadNode(session, path.nodes()) ;
	}

	public ListIterable<ReadRelationship> relationships(){
		return new IterableReadRelation(session, path.relationships()) ;
	}

}


class WriteNeoPath extends NeoPath<WriteNode, WriteRelationship>{

	private WriteSession session ;
	private Path path ;
	private WriteNeoPath(WriteSession session, Path path) {
		super(path) ;
		this.session = session ;
		this.path = path ;
	}

	public static WriteNeoPath load(WriteSession rsession, Path path) {
		return new WriteNeoPath(rsession, path);
	}

	public int length(){
		return path.length() ;
	}

	public WriteNode startNode(){
		return session.node(path.startNode()) ;
	}

	public WriteNode endNode(){
		return session.node(path.endNode()) ;
	}
	
	public ListIterable<WriteNode> nodes(){
		return new IterableWriteNode(session, path.nodes()) ;
	}

	public ListIterable<WriteRelationship> relationships(){
		return new IterableWriteRelation(session, path.relationships()) ;
	}

}
