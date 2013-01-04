package net.ion.neo;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.neo.util.DebugPrinter;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.IndexHits;

public class RelationshipCursor <R extends NeoRelationship> extends ListIterable<R> implements Iterator<R> {

	private NeoSession<?, R> session ;
	private Iterator<Relationship> iter ;
	private List<Relationship> hits ;
	private RelationshipCursor(NeoSession<?, R> session, List<Relationship> hits) {
		this.session = session ;
		this.hits = hits ;
		this.iter = hits.iterator() ;
	}

	static <R extends NeoRelationship> RelationshipCursor<R> create(NeoSession<?, R> session, List<Relationship> hits) {
		return new RelationshipCursor<R>(session, hits);
	}

	public void debugPrint(Page page) {
		each(page, new DebugPrinter<R>());
	}

	public R next(){
		return session.relation(iter.next());
	}


	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported") ;
	}

	public int count() {
		return hits.size();
	}


	@Override
	public Iterator<R> iterator() {
		return this;
	}

}

