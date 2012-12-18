package net.ion.neo;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Relationship;

public class WriteIterableRealtion implements ListIterable<WriteRelationship> {

	private WriteIteratorRelation iterator ;
	WriteIterableRealtion(WriteSession wsession, Iterable<Relationship> iterable) {
		this.iterator = new WriteIteratorRelation(wsession, iterable.iterator()) ;
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


class WriteIteratorRelation implements Iterator<WriteRelationship>{
	
	private WriteSession wsession ;
	private Iterator<Relationship> iterator ;
	
	WriteIteratorRelation(WriteSession wsession, Iterator<Relationship> iterator) {
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