package net.ion.neo;

import java.io.IOException;
import java.util.concurrent.Future;

public class NeoSession {

	private final Credential credential ;
	private NeoWorkspace wspace ;
	
	NeoSession(Credential credential, NeoWorkspace wspace) {
		this.credential = credential ;
		this.wspace = wspace ;
	}

	public ReadNode rootNode() {
		return wspace.rootNode(this) ;
	}

	public NeoWorkspace currentWorkspace() {
		return wspace ;
	}
	
	public Credential credential(){
		return credential ; 
	}

	public void dropWorkspace() throws IOException {
		wspace.clear() ;
	}

	public <T> Future<T> tran(TransactionJob<T> tjob) {
		return wspace.tran(this, tjob) ;
		
	}

	public SessionQuery createQuery() {
		return SessionQuery.create(wspace, this) ;
	}

}
