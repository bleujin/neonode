package net.ion.neo;

import java.util.Iterator;

import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Node;

public class IterableNeoNode {
	// this class hidden outer
}


class IterableReadNode extends ListIterable<ReadNode> {

	private IteratorReadNode iterator ;
	IterableReadNode(ReadSession rsession, Iterable<Node> iterable) {
		this.iterator = new IteratorReadNode(rsession, iterable.iterator()) ;
	}

	@Override
	public Iterator<ReadNode> iterator() {
		return iterator;
	}


}

class IteratorReadNode implements Iterator<ReadNode> {

	private ReadSession rsession ;
	private Iterator<Node> iterator ;
	IteratorReadNode(ReadSession rsession, Iterator<Node> iterator) {
		this.rsession = rsession ;
		this.iterator = iterator ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public ReadNode next() {
		return rsession.node(iterator.next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("exception.unsupported.read") ;
	}
}


class IterableWriteNode extends ListIterable<WriteNode> {

	private IteratorWriteNode iterator ;
	IterableWriteNode(WriteSession rsession, Iterable<Node> iterable) {
		this.iterator = new IteratorWriteNode(rsession, iterable.iterator()) ;
	}

	@Override
	public Iterator<WriteNode> iterator() {
		return iterator;
	}


}

class IteratorWriteNode implements Iterator<WriteNode> {

	private WriteSession rsession ;
	private Iterator<Node> iterator ;
	IteratorWriteNode(WriteSession rsession, Iterator<Node> iterator) {
		this.rsession = rsession ;
		this.iterator = iterator ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public WriteNode next() {
		return rsession.node(iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove() ;
	}
}
