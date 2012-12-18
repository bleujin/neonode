package net.ion.neo;

import junit.framework.TestCase;

public class TestNeoNodeBase extends TestCase{

	protected NeoRepository rep;
	protected NeoSession session ;

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

}
