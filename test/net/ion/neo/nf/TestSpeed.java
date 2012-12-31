package net.ion.neo.nf;

import java.net.URL;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.neo.NodeCursor;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

public class TestSpeed extends TestNeoNodeBase {

	public void setUp() throws Exception {
		super.setUp();
//		session.dropWorkspace();
	}

	public void testInsert() throws Exception {

		long start = System.currentTimeMillis();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				final String rstring = RandomUtil.nextRandomString(100);
				for (int i = 0; i < 10000; i++) {
					tsession.newNode().property("idx", i).property("rnd", rstring);
				}
				return null;
			}
		}).get();

		Debug.line(System.currentTimeMillis() - start, "10000 insert"); //2719
	}
	
	public void testRelation() throws Exception {
		long start = System.currentTimeMillis();
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				NodeCursor<WriteNode> nc = tsession.createQuery().find();
				final WriteNode root = tsession.rootNode();
				nc.each(new Closure<WriteNode>() {
					@Override
					public void execute(WriteNode target) {
						root.createRelationshipTo(target, RelType.CHILD) ;
					}
				}) ;
				return null ;
			}
		}).get();
		Debug.line(System.currentTimeMillis() - start, "10000 insert"); //32439
	}
	
	public void testConfirm() throws Exception {
		
		Debug.line(session.createQuery().find().count()) ;
	}
	

	public void testInsertChild() throws Exception {

		long start = System.currentTimeMillis();
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				final String rstring = RandomUtil.nextRandomString(100);
				for (int i = 0; i < 10000; i++) {
					tsession.rootNode().createRelationNode(RelType.CHILD, i + "scv").property("idx", i).property("rnd", rstring);
				}
				return null;
			}
		}).get();

		Debug.line(System.currentTimeMillis() - start, "10000 insert"); // merge:32439, create:3685
	}

	
	public void testSelect() throws Exception {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000 ; i++) {
			session.findById(RandomUtil.nextInt(10000)) ;
		}
		Debug.line(System.currentTimeMillis() - start, "10000 findById"); // 96 in
	}
	
	
	public void testJar() throws Exception {
		URL url = this.getClass().getResource("/net/ion/framework/db/Rows.class");
		Debug.line(url) ;
	}
	
}
