package net.ion.neo.node;

import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.neo.ReadNode;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;

public class TestFirstCUD extends TestNeoNodeBase {
	
	public void testCreate() throws Exception {
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
	
	public void testUpdate() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode newNode = tsession.newNode();
				newNode.property("name", "bleujin").property("age", 20) ;
				return null;
			}
		}).get() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode found = tsession.createQuery().parseQuery("name:bleujin").findOne() ;
				found.property("name", "hero") ;
				return null;
			}
		}).get() ;
		
		session.createQuery().find().debugPrint(Page.ALL) ;
	}

	public void testDelete() throws Exception {
		session.workspace().clear() ;
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode newNode = tsession.rootNode().mergeChild("child") ;
				newNode.property("name", "bleujin").property("age", 20) ;
				
				newNode.mergeChild("grandchild").property("name", "hero") ;
				return null;
			}
		}).get() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode found = tsession.createQuery().parseQuery("name:bleujin").findOne() ;
				found.remove() ;
				return null;
			}
		}).get() ;
		
		session.createQuery().find().debugPrint(Page.ALL) ;
	}
	
	
	public void testEventHandler() throws Exception {
		session.workspace().registerTransactionHandler(new TransactionEventHandler<Object>(){
			@Override
			public void afterCommit(TransactionData tdata, Object obj) {
				Debug.line("afterCommit", obj, tdata.assignedNodeProperties(), tdata.assignedRelationshipProperties()) ;
			}

			@Override
			public void afterRollback(TransactionData arg0, Object arg1) {
				Debug.line(arg0, arg1) ;
			}

			@Override
			public Object beforeCommit(TransactionData tdata) throws Exception {
				Debug.line("beforeCommit", tdata.assignedNodeProperties(), tdata.assignedRelationshipProperties()) ;
				return null;
			}
		}) ;
		
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode newNode = tsession.rootNode().mergeChild("child") ;
				newNode.property("name", "bleujin").property("age", 20) ;
				
				newNode.mergeChild("grandchild").property("name", "hero") ;
				return null;
			}
		}).get() ;
		
	}
}
