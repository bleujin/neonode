package net.ion.neo.bleujin;

import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.Debug;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class TestThreadSafe extends TestBase {

	public void testThread() throws Exception {
		
		List<ClientThread> cts = new ArrayList<ClientThread>() ;
		for (int i = 0; i < 3 ; i++) {
			cts.add(new ClientThread("c1", graphDB)) ;
		}
		
		for (ClientThread ct : cts) {
			ct.start() ;
		}
		
		
		for (ClientThread ct : cts) {
			ct.join();
		}
		
		for (Node n : graphDB.getAllNodes()) {
			Debug.line(n) ;
		}
	}
	
	
	public void testSession() throws Exception {

		for (Node n : graphDB.getAllNodes()) {
			Debug.line(n) ;
		} ;

		Transaction tran = graphDB.beginTx();
		Node node = graphDB.createNode() ;
		node.setProperty("name", "bleujin") ;
		
		for (Node n : graphDB.getAllNodes()) {
			Debug.line(n) ;
		} ;
		
		tran.failure() ;
		tran.finish() ;

		for (Node n : graphDB.getAllNodes()) {
			Debug.line(n) ;
		} ;

	}
	
}

class ClientThread extends Thread {

	private GraphDatabaseService graphDB ;
	public ClientThread(String name, GraphDatabaseService graphDB) {
		super(name) ;
		this.graphDB = graphDB ;
	}
	
	public void run(){
		Transaction tran = graphDB.beginTx();
		try {
			graphDB.createNode() ;
			Thread.sleep(1000) ;
			tran.success();
		} catch(Throwable ex){
			ex.printStackTrace() ;
		} finally {
			tran.finish();
		}
	}
}
