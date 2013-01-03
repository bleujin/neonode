package net.ion.neo;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Relationship;


public class IterableNeoRelation {
	// this class hidden outer
}



class IterableReadRelation extends ListIterable<ReadRelationship> {

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
		throw new UnsupportedOperationException("exception.unsupported.read") ;
	}
}

class IterableWriteRelation extends ListIterable<WriteRelationship> {

	private IteratorWriteRelation iterator ;
	IterableWriteRelation(WriteSession wsession, Iterable<Relationship> iterable) {
		this.iterator = new IteratorWriteRelation(wsession, iterable.iterator()) ;
	}
	
	@Override
	public Iterator<WriteRelationship> iterator() {
		return iterator;
	}
	

	@Override
	public List<WriteRelationship> toList() {
		List<WriteRelationship> result = ListUtil.newList();
		for (WriteRelationship rs : this) {
			result.add(rs) ;
		}
		return result;
	}

}


class IteratorWriteRelation implements Iterator<WriteRelationship>{
	
	private WriteSession wsession ;
	private Iterator<Relationship> iterator ;
	
	IteratorWriteRelation(WriteSession wsession, Iterator<Relationship> iterator) {
		this.wsession = wsession ;
		this.iterator = iterator ;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public WriteRelationship next() {
		return WriteRelationship.load(wsession, iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove() ;
	}

}