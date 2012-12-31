package net.ion.neo.node;

import java.util.Collections;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;
import net.ion.neo.NodeCursor;
import net.ion.neo.ReadNode;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;

public class TestPaging extends TestNeoNodeBase{

	public void testCreate() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				final List<Integer> list = ListUtil.rangeNum(110);
				Collections.shuffle(list) ;
				
				for (int i : list) {
					WriteNode newNode = tsession.newNode();
					newNode.property("idx", i).property("name", "bleujin");
				}
				return null;
			}
		}).get() ;
		
		NodeCursor<ReadNode> nc = session.createQuery().parseQuery("name:bleujin").ascending("idx").skip(10).offset(10).find();
		assertEquals(10, nc.toList().get(0).property("idx")) ;
		assertEquals(19, nc.toList().get(9).property("idx")) ;
		
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				tsession.newNode().property("idx", -1).property("name", "bleujin");
				
				NodeCursor<ReadNode> nc = session.createQuery().parseQuery("name:bleujin").ascending("idx").skip(10).offset(10).find();
				assertEquals(9, nc.toList().get(0).property("idx")) ;
				assertEquals(18, nc.toList().get(9).property("idx")) ;
				
				nc.debugPrint() ;
				return null;
			}
		}).get() ;
	}
	
	
	
	
	
	
}