package net.ion.neo;

import java.awt.image.RescaleOp;
import java.util.Iterator;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Relationship;

public class ReadIterableRelation implements ListIterable<ReadRelationship> {

	private ReadIteratorRelation iterator ;
	ReadIterableRelation(NeoSession rsession, Iterable<Relationship> iterable) {
		this.iterator = new ReadIteratorRelation(rsession, iterable.iterator()) ;
	}

	@Override
	public Iterator<ReadRelationship> iterator() {
		return iterator;
	}

	@Override
	public List<ReadRelationship> toList() {
		List<ReadRelationship> result = ListUtil.newList();
		for (ReadRelationship rs : this) {
			result.add(rs) ;
		}
		return result;
	}

}

class ReadIteratorRelation implements Iterator<ReadRelationship> {

	private NeoSession rsession ;
	private Iterator<Relationship> iterator ;
	ReadIteratorRelation(NeoSession rsession, Iterator<Relationship> iterator) {
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




