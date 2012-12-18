package net.ion.neo;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.neo4j.graphdb.Direction;

import net.ion.framework.util.Debug;
import net.ion.neo.NeoWorkspace.RelType;
import net.ion.neo.util.ListIterable;
import junit.framework.TestCase;

public class HelloWorld extends TestCase {

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

	public void testCreateGraph() throws Exception {
		NeoSession session = rep.testLogin("test");
		Future<Void> future = session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode root = tsession.rootNode();
				
				WriteNode hello = root.mergeChild("hello") ;
				hello.property("pkey", "Hello") ;
				
				WriteNode world = root.mergeChild("world") ;
				world.property("pkey", "World") ;
				
				WriteRelationship relation = hello.createRelationshipTo(world, RelType.create("KNOW"));
				relation.property("msg", " ") ;
				
				return null;
			}
		});
		
		future.get() ;
		
		ReadNode root = session.rootNode();
		ListIterable<ReadRelationship> rels = root.relationShips(Direction.OUTGOING, RelType.CHILD);
		
		Debug.line(rels.toList().toArray()) ;
	}
	
}
