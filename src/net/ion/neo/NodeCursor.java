package net.ion.neo;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.neo.util.DebugPrinter;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Node;

public class NodeCursor<T extends NeoNode, R extends NeoRelationship> extends ListIterable<T> implements Iterator<T> {

	private NeoSession<T, R> session ;
	private Iterator<Node> iter ;
	private List<Node> hits ;
	private NodeCursor(NeoSession<T, R> session, List<Node> hits) {
		this.session = session ;
		this.hits = hits ;
		this.iter = hits.iterator() ;
	}

	static <T extends NeoNode, R extends NeoRelationship> NodeCursor<T, R> create(NeoSession<T, R> session, List<Node> hits) {
		return new NodeCursor<T, R>(session, hits);
	}

	public T next(){
		return session.node(iter.next());
	}
	
	public int count() {
		return hits.size();
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported") ;
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}


}
