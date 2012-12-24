package net.ion.neo.relation;

import net.ion.neo.NeoConstant;
import net.ion.neo.ReadRelationship;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteRelationship;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.graphdb.Direction;

public class TestFirstRelationShip extends TestNeoNodeBase{

	public void testCreateRelation() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode hero = tsession.rootNode().mergeChild("emanon").property("name", "hero");
				WriteNode bleujin = tsession.newNode().property("name", "bleujin");
				
				WriteRelationship rel = bleujin.createRelationshipTo(hero, RelType.create("friend"));
				
				return null;
			}
		}).get() ;
		
		assertEquals(1, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
		
		ReadRelationship rel = session.relationshipQuery().findOne();
		assertEquals(NeoConstant.DefaultRelationName, rel.property(NeoConstant.RelationName)) ;
	}
	
	
	public void testFindRelation() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode bleujin = tsession.newNode().property("name", "bleujin");
				
				tsession.rootNode().createRelationshipTo(bleujin, RelType.create("friend"));
				return null;
			}
		}).get() ;
		
		assertEquals(NeoConstant.DefaultRelationName, session.createQuery().parseQuery("name:bleujin").findOne().relationShips(Direction.INCOMING).first().property(NeoConstant.RelationName) ) ;
		assertEquals(NeoConstant.DefaultRelationName, session.createQuery().parseQuery("name:bleujin").findOne().firstRelationShip(Direction.INCOMING).property(NeoConstant.RelationName) ) ;
	}
	
	public void testDeleteRelation() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode bleujin = tsession.newNode().property("name", "bleujin");
				tsession.rootNode().createRelationshipTo(bleujin, RelType.create("friend"));
				return null;
			}
		}).get() ;

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				tsession.rootNode().relationShips(Direction.OUTGOING).first().remove() ;
				return null ;
			}
		}).get() ;

		assertEquals(0, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
		assertEquals(1, session.createQuery().find().count()) ;
	}
	
	
	
	
	
	
}
