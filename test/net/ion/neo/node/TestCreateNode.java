package net.ion.neo.node;

import net.ion.neo.NeoConstant;
import net.ion.neo.ReadNode;
import net.ion.neo.ReadRelationship;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.graphdb.Direction;

public class TestCreateNode extends TestNeoNodeBase {
	
	public void testNewNode() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode newNode = tsession.newNode();
				newNode.property("name", "bleujin").property("age", 20) ;
				return null;
			}
		}).get() ;
		
		ReadNode found = session.createQuery().parseQuery("name:bleujin").findOne() ;
		assertEquals("bleujin", found.property("name")) ;
		assertEquals(20, found.property("age")) ;
	}
	
	public void testMergeChild() throws Exception {
		session.dropWorkspace() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode newNode = tsession.rootNode().mergeRelationNode(RelType.CHILD, "bleujin") ;
				newNode.property("age", 20) ;
				return null;
			}
		}).get() ;
		
		ReadRelationship rel = session.rootNode().firstRelationShip(Direction.OUTGOING, RelType.CHILD);
		assertEquals("bleujin", rel.property(NeoConstant.RelationName)) ;

		ReadNode endNode = rel.endNode();
		assertEquals(20, endNode.property("age")) ;
	}
	
	
	
	
}
