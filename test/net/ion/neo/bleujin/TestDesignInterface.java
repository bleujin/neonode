package net.ion.neo.bleujin;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Relationship;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.neo.NeoConstant;
import net.ion.neo.NeoRepository;
import net.ion.neo.ReadNode;
import net.ion.neo.ReadRelationship;
import net.ion.neo.ReadSession;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteRelationship;
import net.ion.neo.WriteSession;

public class TestDesignInterface extends TestCase {

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

	public void testSession() throws Exception {
		ReadSession session = rep.testLogin("test");

		ReadNode root = session.rootNode();
		Debug.line(root);
		session.dropWorkspace() ;
	}

	public void testTwoSession() throws Exception {
		ReadSession ns1 = rep.testLogin("test1");
		ReadNode root1 = ns1.rootNode();
		Debug.line(root1);
		
		ReadSession ns2 = rep.testLogin("test2");
		ReadNode root2 = ns2.rootNode();
		Debug.line(root2);
		
		ns2.dropWorkspace() ;
	}
	
	public void testRootNode() throws Exception {
		ReadSession session = rep.testLogin("test");
		ReadNode root = session.rootNode();
		
		assertEquals(root, session.rootNode()) ;
		session.dropWorkspace() ;
	}

	public void testProperty() throws Exception {
		ReadSession session = rep.testLogin("test");
		
		session.tran(new TransactionJob<Integer>(){
			public Integer handle(WriteSession ws){
				WriteNode root = ws.rootNode();
				WriteNode node = root.mergeChild("c1");
				node.property("name", "bleujin") ;
				
				WriteRelationship rs = root.firstRelationShip(Direction.OUTGOING);
				assertEquals("c1", rs.property(NeoConstant.RelationName)) ;
				
				return 1 ;
			}
		}).get() ;
				
		Iterable<ReadRelationship> rels = session.rootNode().relationShips(Direction.OUTGOING);
		for (ReadRelationship rel : rels) {
			Debug.debug(rel.keys(), rel.startNode(), rel.endNode(), rel.endNode().keys()) ;
		}
		
		session.dropWorkspace() ;
	}
	
	
	
	
	

}
