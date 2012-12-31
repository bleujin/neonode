package net.ion.neo.relation;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.isearcher.common.MyField;
import net.ion.neo.NeoConstant;
import net.ion.neo.ReadRelationship;
import net.ion.neo.RelationCursor;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteRelationship;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.neo4j.graphdb.Direction;

public class TestRemoveRelation extends TestNeoNodeBase {

	public void testRemoveRelation() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode hero = tsession.rootNode().mergeRelationNode(RelType.CHILD, "emanon").property("name", "hero");
				WriteNode bleujin = tsession.newNode().property("name", "bleujin");
				
				WriteRelationship rel = bleujin.createRelationshipTo(hero, RelType.create("friend"));
				return null;
			}
		}).get() ;
		
		assertEquals(1, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
		
		ReadRelationship rel = session.relationshipQuery().findOne();
		assertEquals(NeoConstant.DefaultRelationName, rel.property(NeoConstant.RelationName)) ;
		
		
		session.tran(new TransactionJob<Void>() {

			@Override
			public Void handle(WriteSession tsession) {
				RelationCursor<WriteRelationship> rc = tsession.relationshipQuery().parseQuery(NeoConstant.RelationName + ":" + NeoConstant.DefaultRelationName).find();
				for (WriteRelationship r : rc.toList(Page.ALL)) {
					r.remove() ;
				}
				return null;
			}
		}).get() ;

		assertEquals(0, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
	}
	
	
	public void testTwiceRelation() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode hero = tsession.newNode().property("name", "hero");
				
				tsession.rootNode().createRelationshipTo(hero, RelType.CHILD) ;
				tsession.rootNode().createRelationshipTo(hero, RelType.CHILD) ;

				return null;
			}
		}).get() ;
		
		assertEquals(2, session.relationshipQuery().find().count()) ;
		assertEquals(2, session.rootNode().relationShips(Direction.OUTGOING).toList().size()) ;
	}
	
	
	public void testDocument() throws Exception {
		Document document = new Document() ;
		for (int i = 0; i < 3; i++) {
			MyField field = MyField.unknown("key", "val") ;
			document.add(field.getRealField()) ;
			for (Fieldable f : field.getMoreField()) {
				document.add(f) ;
			}
		}
		
		Debug.line(document.getFields()) ;
		
	}
	
	public void testFindRelation() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode hero = tsession.rootNode().mergeRelationNode(RelType.CHILD, "hero") ;
				return null;
			}
		}).get() ;
		
		assertEquals(1, session.relationshipQuery().parseQuery("hero").find().toList(Page.ALL).size()) ;
		
	}
	
	
	
	
	
	
	
}
