package net.ion.neo;

import java.util.List;
import java.util.Map;

import net.ion.framework.schedule.IExecutor;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class NeoRepository {

	
	private IExecutor executor = new IExecutor(0, 3) ;
	private Map<String, NeoWorkspace> wss = MapUtil.newCaseInsensitiveMap() ;

	public void shutdown() {
		for (NeoWorkspace ws : wss.values()) {
			ws.close() ;
		}
	}

	public IExecutor executor(){
		return executor ;
	}
	
	public NeoSession testLogin(String wsname) {
		return login(Credential.EMANON, wsname) ;
	}
	
	public NeoSession login(Credential credential, String wsname) {
		return new NeoSession(credential, loadWorkspce(wsname));
	}
	
	private synchronized NeoWorkspace loadWorkspce(String wsname){
		if (wss.containsKey(wsname)){
			return wss.get(wsname) ;
		} else {
			wss.put(wsname, NeoWorkspace.create(this, "./resource/" + wsname)) ;
			return wss.get(wsname) ;
		}
	}
}
