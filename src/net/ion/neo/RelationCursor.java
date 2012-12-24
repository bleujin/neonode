package net.ion.neo;

import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.neo.util.DebugPrinter;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.IndexHits;

public class RelationCursor <T extends NeoRelationship> {

	private NeoSession<?, T> session ;
	private IndexHits<Relationship> hits ;
	private RelationCursor(NeoSession session, IndexHits<Relationship> hits) {
		this.session = session ;
		this.hits = hits ;
	}

	static RelationCursor create(NeoSession session, IndexHits<Relationship> hits) {
		return new RelationCursor(session, hits);
	}

	public void debugPrint(Page page) {
		each(page, new DebugPrinter<T>());
	}

	public T next(){
		return session.relation(hits.next());
	}
	
	public void each(Page page, Closure closure) {
		CollectionUtil.each(toList(page), closure);
	}
	
	public void close(){
		hits.close() ;
	}
	
	public List<T> toList(Page page) {
		int pageIndexOnScreen = page.getPageNo() - page.getMinPageNoOnScreen() ;
		return toList(pageIndexOnScreen * page.getListNum(), page.getListNum());
	}

	public List<T> toList(int skip, int limit) {
		while (skip-- > 0) {
			if (hits.hasNext()) {
				hits.next();
			} else {
				return ListUtil.EMPTY;
			}
		}
		List<T> result = ListUtil.newList();
		while (limit-- > 0 && hits.hasNext()) {
			result.add((T)session.relation(hits.next()));
		}
		return result;
	}

	public int count() {
		return hits.size();
	}

	public T first() {
		if (! hits.hasNext()){
			return null ;
		} else {
			return (T) session.relation(hits.next()) ;
		}
	}

}

