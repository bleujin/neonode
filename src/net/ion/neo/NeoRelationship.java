package net.ion.neo;

import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class NeoRelationship {

	private Relationship relationShip ;
	protected NeoRelationship(Relationship relationship){
		this.relationShip = relationship ;
	}
	
	protected Relationship relationShip(){
		return relationShip ;
	}

	public boolean has(String pkey){
		return relationShip().hasProperty(pkey) ;
	}
	
	public Object property(String pkey) {
		return relationShip().getProperty(pkey);
	}

	public Iterable<String> keys(){
		return relationShip().getPropertyKeys() ;
	}

	public RelationshipType type(){
		return relationShip().getType() ;
	}
	
	public boolean isType(RelationshipType type){
		return relationShip().isType(type) ;
	}

	public int hashCode(){
		return relationShip().hashCode() ;
	}
	
	public boolean equals(Object obj){
		if (obj instanceof ReadRelationship){
			ReadRelationship that = (ReadRelationship) obj ;
			return this.relationShip().equals(that.relationShip()) ;
		} else {
			return false ;
		}
	}
	public String toString(){
		Iterable<String> keysIter = relationShip().getPropertyKeys();
		Map<String, Object> map = net.ion.framework.util.MapUtil.newMap() ; 
		for (String key : keysIter) {
			map.put(key, relationShip().getProperty(key)) ;
		}
		return JsonObject.fromObject(map).toString() ;
	}

	
}
