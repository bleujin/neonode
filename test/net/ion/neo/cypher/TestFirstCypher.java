package net.ion.neo.cypher;

import java.util.Iterator;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;

public class TestFirstCypher extends TestNeoNodeBase{

	public void setUp() throws Exception {
		super.setUp() ;
		session.dropWorkspace() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				WriteNode root = tsession.rootNode();
				root.property("name", "root") ;
				
				WriteNode bleujin = root.mergeRelationNode(RelType.CHILD, "bleujin").property("name", "bleujin").property("age", 20).property("text", "태극기가 바람에 펄럭입니다");
				WriteNode hero = root.mergeRelationNode(RelType.CHILD, "hero").property("name", "hero").property("age", 25).propertyWithoutIndex("noindex", 3);
				
				hero.mergeRelationNode(RelType.CHILD, "jin").property("name", "jin").property("age", 25).property("val", 3);
				
				bleujin.createRelationshipTo(hero, RelType.create("know")).property("type", "friend") ;
				return null;
			}
		}).get() ;
	}
	
	public void testQuery() throws Exception {
		ExecutionEngine engine = session.executionEngine() ;
		
		Map<String, Object> params = MapUtil.newMap() ;
		params.put( "id", 0 );
		
		long start = System.currentTimeMillis() ;
		ExecutionResult result = engine.execute("start n=node({id}) match (n)--> (x) return n.name, x.name", params);
		Debug.line(System.currentTimeMillis() - start) ;
		
		Debug.line(result.getQueryStatistics(), result.columns(), result) ;
		
		Iterator<Map<String, Object>> iter = result.iterator();
		
		for (Map<String, Object> map : result) {
			Debug.line(map) ;
		}

	}
}
