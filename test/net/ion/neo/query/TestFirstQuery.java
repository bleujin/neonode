package net.ion.neo.query;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.neo.NodeCursor;
import net.ion.neo.ReadNode;
import net.ion.neo.ReadRelationship;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

public class TestFirstQuery extends TestNeoNodeBase {


	public void setUp() throws Exception {
		super.setUp() ;
		session.dropWorkspace() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				WriteNode root = tsession.rootNode();
				
				WriteNode bleujin = root.mergeRelationNode(RelType.CHILD, "bleujin").property("name", "bleujin").property("age", 20).property("text", "ÅÂ±Ø±â°¡ ¹Ù¶÷¿¡ ÆÞ·°ÀÔ´Ï´Ù");
				WriteNode hero = root.mergeRelationNode(RelType.CHILD, "hero").property("name", "hero").property("age", 25).propertyWithoutIndex("noindex", 3);
				
				bleujin.createRelationshipTo(hero, RelType.create("know")).property("type", "friend") ;
				return null;
			}
		}).get() ;
	}
	
	public void testParseQuery() throws Exception {
		NodeCursor<ReadNode, ReadRelationship> nc = session.createQuery().parseQuery("age:[20 TO 30]").find();
		assertEquals(2, nc.count()) ;
		nc.debugPrint() ;
	}

	
	public void testTextTermQuery() throws Exception {
		NodeCursor<ReadNode, ReadRelationship> nc = session.createQuery().parseQuery("text:ÅÂ±Ø±â").find();
		assertEquals(1, nc.count()) ;
	}
	
	public void testPropertyWithoutIndex() throws Exception {
		long start = System.nanoTime() ;
		NodeCursor<ReadNode, ReadRelationship> nc = session.createQuery().parseQuery("noindex:3").find();
		assertEquals(0, nc.count()) ;
		Debug.line(System.nanoTime() - start) ;
	}


	public void testSort() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				for (int i : ListUtil.rangeNum(10)) {
					tsession.newNode().property("name", "all").property("index", RandomUtil.nextInt(10)) ;
				}
				tsession.newNode().property("name", "all").property("index", 0) ;
				return null;
			}
		}).get() ;
		
		NodeCursor<ReadNode, ReadRelationship> nc = session.createQuery().parseQuery("name:all").ascending("index").topDoc(10).find();
		final List<ReadNode> list = nc.toList();
		assertEquals(10, list.size()) ;
		
		assertEquals(0, list.get(0).property("index")) ;
	}
	
	
}
