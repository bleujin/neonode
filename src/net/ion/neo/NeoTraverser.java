package net.ion.neo;

import java.util.Iterator;

import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Traverser;

public class NeoTraverser<T extends NeoNode, R extends NeoRelationship> extends ListIterable<NeoPath<T,R>>{

	private Iterator<NeoPath<T, R>> iterator ;
	private NeoTraverser(Iterator<NeoPath<T, R>> iterator) {
		this.iterator = iterator ;
	}

	static <T extends NeoNode, R extends NeoRelationship> NeoTraverser<T, R> create(ReadSession session, Traverser traverser){
		return new NeoTraverser(new IteratorReadNeoPath(session, traverser)) ;
	}
	
	static <T extends NeoNode, R extends NeoRelationship> NeoTraverser<T, R> create(WriteSession wsession, Traverser traverser) {
		return new NeoTraverser(new IteratorWriteNeoPath(wsession, traverser)) ;
	}
	
	@Override
	public Iterator<NeoPath<T, R>> iterator() {
		return iterator;
	}

}


class IteratorReadNeoPath implements Iterator<NeoPath<ReadNode, ReadRelationship>> {

	private ReadSession rsession ;
	private Iterator<Path> iterator ;
	IteratorReadNeoPath(ReadSession rsession, Iterable<Path> iterator) {
		this.rsession = rsession ;
		this.iterator = iterator.iterator() ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public ReadNeoPath next() {
		return ReadNeoPath.load(rsession, iterator.next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("exception.unsupported.read") ;
	}
}


class IteratorWriteNeoPath implements Iterator<NeoPath<WriteNode, WriteRelationship>> {

	private WriteSession rsession ;
	private Iterator<Path> iterator ;
	IteratorWriteNeoPath(WriteSession rsession, Iterable<Path> iterator) {
		this.rsession = rsession ;
		this.iterator = iterator.iterator() ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public WriteNeoPath next() {
		return WriteNeoPath.load(rsession, iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove() ;
	}
}