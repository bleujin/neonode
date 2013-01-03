package net.ion.neo;

import java.util.Map;

import net.ion.framework.schedule.IExecutor;
import net.ion.framework.util.MapUtil;

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
	
	public ReadSession testLogin(String wsname) {
		return login(Credential.EMANON, wsname) ;
	}
	
	public ReadSession login(Credential credential, String wsname) {
		return new ReadSession(credential, loadWorkspce(wsname));
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
