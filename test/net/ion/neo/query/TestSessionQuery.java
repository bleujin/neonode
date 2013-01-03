package net.ion.neo.query;

import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.neo.NodeCursor;
import net.ion.neo.ReadNode;
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
		NodeCursor<ReadNode> nc = session.createQuery().ascending("idx").find();
		for (ReadNode node : nc) {
			Debug.line(node) ;
		}
	}
	
	public void testSkip() throws Exception {
		long start = System.currentTimeMillis() ;
		NodeCursor<ReadNode> nc = session.createQuery().ascending("idx").skip(2).offset(5).topDoc(5).tradeForSpeed(true).find();
		nc.debugPrint() ;
		
		assertEquals(3, nc.count()) ;
		assertEquals(2, nc.first().property("idx")) ;
		assertEquals(4, nc.toList().get(2).property("idx")) ;
		Debug.line(System.currentTimeMillis() - start) ;
	}

	
	
	

}
