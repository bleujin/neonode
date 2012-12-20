package net.ion.neo;

import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;

public class NodeCursor<T extends NeoNode> {

	private NeoSession<T> session ;
	private IndexHits<Node> hits ;
	private NodeCursor(NeoSession session, IndexHits<Node> hits) {
		this.session = session ;
		this.hits = hits ;
	}

	public static <F extends NeoNode> NodeCursor<F> create(NeoSession session, IndexHits<Node> hits) {
		return new NodeCursor(session, hits);
	}

	public void debugPrint(Page page) {
		each(page, new DebugPrinter<T>());
	}

	public T next(){
		return session.node(hits.next());
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
			result.add((T)session.node(hits.next()));
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
			return (T) session.node(hits.next()) ;
		}
	}

}

class DebugPrinter<T> implements Closure<T>{
	public void execute(T obj) {
		Debug.debug(obj, obj.getClass()) ;
	}
}