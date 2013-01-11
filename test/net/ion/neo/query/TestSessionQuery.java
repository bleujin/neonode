package net.ion.neo.query;

import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.neo.NodeCursor;
import net.ion.neo.ReadNode;
import net.ion.neo.ReadRelationship;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

public class TestSessionQuery extends TestNeoNodeBase {

	public void setUp() throws Exception {
		super.setUp() ;
		session.dropWorkspace() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				WriteNode root = tsession.rootNode();
				
				for (int i = 0; i < 15 ; i++) {
					WriteNode bleujin = root.createRelationNode(RelType.CHILD, "bleujin").property("idx", i).property("rani", RandomUtil.nextInt(10)).property("name", "bleujin").property("age", 20);
				}
				return null;
			}
		}).get() ;
	}
	
	public void testIterable() throws Exception {
		NodeCursor<ReadNode, ReadRelationship> nc = session.createQuery().ascending("idx").find();
		for (ReadNode node : nc) {
			Debug.line(node) ;
		}
	}
	
	public void testSkip() throws Exception {
		long start = System.currentTimeMillis() ;
		NodeCursor<ReadNode, ReadRelationship> nc = session.createQuery().ascending("idx").skip(2).atLength(5).topDoc(5).tradeForSpeed(true).find();
//		nc.debugPrint(Page.ALL) ;
		
		assertEquals(3, nc.count()) ;
		
		List<ReadNode> list = nc.toList();
		
		assertEquals(2, list.get(0).property("idx")) ;
		assertEquals(4, list.get(2).property("idx")) ;
		Debug.line(System.currentTimeMillis() - start) ;
	}

	
	
	

}
