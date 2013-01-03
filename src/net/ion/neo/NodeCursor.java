package net.ion.neo;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.neo.util.DebugPrinter;

import org.neo4j.graphdb.Node;

public class NodeCursor<T extends NeoNode> implements Iterator<T>, Iterable<T> {

	private NeoSession<T, ?> session ;
	private Iterator<Node> iter ;
	private List<Node> hits ;
	private NodeCursor(NeoSession session, List<Node> hits) {
		this.session = session ;
		this.hits = hits ;
		this.iter = hits.iterator() ;
	}

	static NodeCursor create(NeoSession session, List<Node> hits) {
		return new NodeCursor(session, hits);
	}

	public void debugPrint() {
		each(new DebugPrinter<T>());
	}
	
	public void each(Closure<T> closure) {
		CollectionUtil.each(toList(), closure);
	}

	public T next(){
		return session.node(iter.next());
	}
	public void close(){
		//
	}
	
	public List<T> toList() {
		List<T> result = ListUtil.newList() ;
		for (Node node : hits) {
			result.add(session.node(node)) ;
		}
		return result ;
	}

	public int count() {
		return hits.size();
	}

	public T first() {
		if (count() > 0){
			return (T) session.node(hits.get(0)) ;
		} else {
			return null ;
		}
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
