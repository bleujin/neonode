package net.ion.neo;

import junit.framework.TestCase;

import org.neo4j.graphdb.GraphDatabaseService;

public class TestNeoNodeBase extends TestCase{

	protected NeoRepository rep;
	protected ReadSession session ;
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rep = new NeoRepository();
		this.session = rep.testLogin("test") ;
	}

	@Override
	protected void tearDown() throws Exception {
		rep.shutdown();
		super.tearDown();
	}

	protected GraphDatabaseService graphDB(){
		return session.workspace().graphDB() ;
	}
}
