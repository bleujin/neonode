package net.ion.neo;

import java.awt.image.RescaleOp;
import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Relationship;

public class IterableReadRelation extends ListIterable<ReadRelationship> {

	private IteratorReadRelation iterator ;
	IterableReadRelation(ReadSession rsession, Iterable<Relationship> iterable) {
		this.iterator = new IteratorReadRelation(rsession, iterable.iterator()) ;
	}

	@Override
	public Iterator<ReadRelationship> iterator() {
		return iterator;
	}
}

class IteratorReadRelation implements Iterator<ReadRelationship> {

	private ReadSession rsession ;
	private Iterator<Relationship> iterator ;
	IteratorReadRelation(ReadSession rsession, Iterator<Relationship> iterator) {
		this.rsession = rsession ;
		this.iterator = iterator ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public ReadRelationship next() {
		return ReadRelationship.load(rsession, iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove() ;
	}
}




