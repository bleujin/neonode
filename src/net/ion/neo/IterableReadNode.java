package net.ion.neo;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Node;

public class IterableReadNode extends ListIterable<ReadNode> {

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
		iterator.remove() ;
	}
}
