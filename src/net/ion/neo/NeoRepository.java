package net.ion.neo;

import java.util.Map;

import net.ion.framework.schedule.IExecutor;
import net.ion.framework.util.MapUtil;
import net.ion.nsearcher.search.analyzer.MyKoreanAnalyzer;

import org.apache.lucene.analysis.Analyzer;

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
		return login(Credential.EMANON, wsname, MyKoreanAnalyzer.class) ;
	}
	
	public ReadSession login(Credential credential, String wsname, Class<? extends Analyzer> indexAnal) {
		return new ReadSession(credential, loadWorkspce(wsname, indexAnal));
	}
	
	private synchronized NeoWorkspace loadWorkspce(String wsname, Class<? extends Analyzer> indexAnal){
		if (wss.containsKey(wsname)){
			return wss.get(wsname) ;
		} else {
			wss.put(wsname, NeoWorkspace.create(this, "./resource/" + wsname, indexAnal)) ;
			return wss.get(wsname) ;
		}
	}


}
