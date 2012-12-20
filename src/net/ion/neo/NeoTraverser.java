package net.ion.neo;

import java.util.Iterator;

import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Traverser;

public class NeoTraverser implements Iterable<NeoPath>{

	private IteratorNeoPath iterator ;
	private NeoTraverser(ReadSession session, Traverser traversal) {
		this.iterator = new IteratorNeoPath(session, traversal) ;
	}

	static NeoTraverser create(ReadSession session, Traverser traversal){
		return new NeoTraverser(session, traversal) ;
	}
	
	@Override
	public Iterator<NeoPath> iterator() {
		return iterator;
	}

}


class IteratorNeoPath implements Iterator<NeoPath> {

	private ReadSession rsession ;
	private Iterator<Path> iterator ;
	IteratorNeoPath(ReadSession rsession, Iterable<Path> iterator) {
		this.rsession = rsession ;
		this.iterator = iterator.iterator() ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public NeoPath next() {
		return NeoPath.load(rsession, iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove() ;
	}
}