package net.ion.neo;

import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;

public class NodeCursor {

	private NeoSession session ;
	private IndexHits<Node> hits ;
	private NodeCursor(NeoSession session, IndexHits<Node> hits) {
		this.session = session ;
		this.hits = hits ;
	}

	public static NodeCursor create(NeoSession session, IndexHits<Node> hits) {
		return new NodeCursor(session, hits);
	}

	public void debugPrint(Page page) {
		each(page, new DebugPrinter());
	}

	public ReadNode next(){
		return ReadNode.findBy(session, hits.next()) ;
	}
	
	public void each(Page page, Closure closure) {
		CollectionUtil.each(toList(page), closure);
	}
	
	public void close(){
		hits.close() ;
	}
	
	public List<ReadNode> toList(Page page) {
		int pageIndexOnScreen = page.getPageNo() - page.getMinPageNoOnScreen() ;
		return toList(pageIndexOnScreen * page.getListNum(), page.getListNum());
	}

	public List<ReadNode> toList(int skip, int limit) {
		while (skip-- > 0) {
			if (hits.hasNext()) {
				hits.next();
			} else {
				return ListUtil.EMPTY;
			}
		}
		List<ReadNode> result = ListUtil.newList();
		while (limit-- > 0 && hits.hasNext()) {
			result.add(ReadNode.findBy(session, hits.next()));
		}
		return result;
	}

	public int count() {
		return hits.size();
	}

	public ReadNode first() {
		Node inner = hits.getSingle();
		if (inner == null) return null ;
		return ReadNode.findBy(session, inner);
	}

}

class DebugPrinter<T> implements Closure<T>{
	public void execute(T obj) {
		Debug.debug(obj, obj.getClass()) ;
	}
}