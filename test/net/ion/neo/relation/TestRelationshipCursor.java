package net.ion.neo.relation;

import net.ion.framework.db.Page;
import net.ion.neo.ReadRelationship;
import net.ion.neo.RelationshipCursor;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteRelationship;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.graphdb.Direction;

public class TestRelationshipCursor extends TestNeoNodeBase{

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode hero = tsession.newNode().property("name", "hero");
				
				tsession.rootNode().createRelationshipTo(hero, RelType.CHILD).property("rel", "abcd") ;
				tsession.rootNode().createRelationshipTo(hero, RelType.CHILD).property("rel", "defg") ;

				return null;
			}
		}).get() ;
	}

	public void testQuery() throws Exception {
		assertEquals("abcd", session.relationshipQuery().parseQuery("rel:abcd").findOne().property("rel")) ;
		assertEquals("defg", session.relationshipQuery().parseQuery("defg").findOne().property("rel")) ;
	}
	
	public void testRelationshipQuery() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				RelationshipCursor<WriteRelationship> rc = tsession.relationshipQuery().find();
				assertEquals(2, rc.count()) ;
				assertEquals(2, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
				
				final RelationshipCursor<WriteRelationship> findrc = tsession.relationshipQuery().parseQuery("rel:defg").find();
				findrc.next().remove() ;

				return null;
			}
		}).get() ;
		
		final RelationshipCursor<ReadRelationship> rc = session.relationshipQuery().find();
		assertEquals(1, rc.count()) ;
		assertEquals(1, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
		assertEquals("abcd", rc.next().property("rel")) ;
	}
	
	public void xtestAnalyzer() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode hero = tsession.newNode().property("name", "hero");
				tsession.rootNode().createRelationshipTo(hero, RelType.CHILD).property("rel", "abc1") ;
				return null;
			}
		}).get() ;
		
		session.relationshipQuery().parseQuery("abc1").find().debugPrint(Page.ALL) ; //@Todo : notfound - mykorean analyzer
		session.relationshipQuery().parseQuery("abc").find().debugPrint(Page.ALL) ;
		session.relationshipQuery().parseQuery("rel:abc1").find().debugPrint(Page.ALL) ;
	}
	
	
}
