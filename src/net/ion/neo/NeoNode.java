package net.ion.neo;

import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

public abstract class NeoNode {

	private Node inner ;
	protected NeoNode(Node inner){
		this.inner = inner ;
	}
	
	public long getId(){
		return inner.getId() ;
	}
	
	public Object property(String pkey){
		return inner.getProperty(pkey) ;
	}
	
	public Iterable<String> keys(){
		return inner.getPropertyKeys() ;
	}
	
	public boolean has(String pkey){
		return inner.hasProperty(pkey) ;
	}


	public boolean hasRelationShip(Direction direction, RelationshipType... rtypes){
		if (rtypes == null || rtypes.length == 0){
			return inner.hasRelationship(direction) ;
		} else 
			return inner.hasRelationship(direction, rtypes) ;
	}
	

	public String toString(){
		Iterable<String> keysIter = inner.getPropertyKeys();
		Map<String, Object> map = net.ion.framework.util.MapUtil.newMap() ; 
		for (String key : keysIter) {
			map.put(key, inner.getProperty(key)) ;
		}
		return JsonObject.fromObject(map).toString() ;
	}


	
	Node inner() {
		return inner ;
	}
	

	@Override
	public boolean equals(Object obj){
		if (obj instanceof ReadNode){
			return this.inner.equals(((ReadNode) obj).inner()) ;
		}
		return false ;
	}
	
	@Override
	public int hashCode(){
		return inner.hashCode() ;
	}

}
