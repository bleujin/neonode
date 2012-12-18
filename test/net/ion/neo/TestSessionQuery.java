package net.ion.neo;

import java.util.List;

import org.apache.lucene.search.SortField;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.neo.NeoWorkspace.RelType;
import junit.framework.TestCase;

public class TestSessionQuery extends TestNeoNodeBase {


	public void setUp() throws Exception {
		super.setUp() ;
		session.dropWorkspace() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				WriteNode root = tsession.rootNode();
				
				WriteNode bleujin = root.mergeChild("bleujin").property("name", "bleujin").property("age", 20).property("text", "ÅÂ±Ø±â°¡ ¹Ù¶÷¿¡ ÆÞ·°ÀÔ´Ï´Ù");
				WriteNode hero = root.mergeChild("hero").property("name", "hero").property("age", 25).propertyWithoutIndex("noindex", 3);
				
				bleujin.createRelationshipTo(hero, RelType.create("know")).property("type", "friend") ;
				return null;
			}
		}).get() ;
	}

	
	public void testParseQuery() throws Exception {
		NodeCursor nc = session.createQuery().parseQuery("age:[20 TO 30]").find() ;
		assertEquals(2, nc.count()) ;
		nc.debugPrint(Page.ALL) ;
	}
	
	public void testTextTermQuery() throws Exception {
		NodeCursor nc = session.createQuery().parseQuery("text:ÅÂ±Ø±â").find() ;
		assertEquals(1, nc.count()) ;
	}
	
	public void testPropertyWithoutIndex() throws Exception {
		long start = System.nanoTime() ;
		NodeCursor nc = session.createQuery().parseQuery("noindex:3").find() ;
		assertEquals(0, nc.count()) ;
		Debug.line(System.nanoTime() - start) ;
	}


	public void testSort() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				for (int i : ListUtil.rangeNum(10)) {
					tsession.newNode().property("name", "all").property("index", 10 - i) ;
				}
				return null;
			}
		}).get() ;
		
		NodeCursor nc = session.createQuery().parseQuery("name:all").ascending("index", SortField.INT).find() ;
		final List<ReadNode> list = nc.toList(Page.create(10, 1));
		assertEquals(10, list.size()) ;
		
		assertEquals(1, list.get(0).property("index")) ;
		Debug.line(list) ;
	}
	
	
	
	
	
}
