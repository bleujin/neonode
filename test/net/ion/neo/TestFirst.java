package net.ion.neo;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.neo4j.graphdb.Direction;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.neo.NeoWorkspace.RelType;
import net.ion.neo.util.ListIterable;
import junit.framework.TestCase;

public class TestFirst extends TestCase {

	private NeoRepository rep;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rep = new NeoRepository();
		
	}

	@Override
	protected void tearDown() throws Exception {
		rep.shutdown();
		super.tearDown();
	}

	public void testCreateGet() throws Exception {
		ReadSession session = rep.testLogin("test");
		session.workspace().clear() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode root = tsession.rootNode();
				
				WriteNode hello = root.mergeRelationNode(RelType.CHILD, "hello") ;
				hello.property("pkey", "Hello") ;
				
				WriteNode world = root.mergeRelationNode(RelType.CHILD, "world") ;
				world.property("pkey", "World") ;
				
				WriteRelationship relation = hello.createRelationshipTo(world, RelType.create("KNOW"));
				relation.property("msg", " ") ;
				return null;
			}
		}).get() ;
		
		session.createQuery().find().debugPrint() ;
		session.rootNode().relationShips(Direction.OUTGOING, RelType.CHILD).debugPrint(Page.ALL) ;
	}
	
}
