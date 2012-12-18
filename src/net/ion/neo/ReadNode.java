package net.ion.neo;

import java.util.Iterator;
import java.util.Map;

import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;

public class ReadNode {

	
	private NeoSession session;
	private Node inner;

	private ReadNode(NeoSession session, Node inner){
		this.session = session ;
		this.inner = inner ;
	}
	
	public static ReadNode findBy(NeoSession session, Node inner) {
		return new ReadNode(session, inner);
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

	public ListIterable<ReadRelationship> relationShips(Direction direction){
		return new ReadIterableRelation(session, inner.getRelationships(direction)) ;
	}
	
	public ListIterable<ReadRelationship> relationShips(Direction direction, RelationshipType rtype){
		return new ReadIterableRelation(session, inner.getRelationships(direction, rtype)) ;
	}
	
	public ListIterable<ReadRelationship> relationShips(Direction direction, RelationshipType... rtypes){
		return new ReadIterableRelation(session, inner.getRelationships(direction, rtypes)) ;
	}
	

	public boolean hasRelationShip(Direction direction){
		return inner.hasRelationship(direction) ;
	}
	
	public boolean hasRelationShip(Direction direction, RelationshipType rtype){
		return inner.hasRelationship(direction, rtype) ;
	}
	
	public boolean hasRelationShip(Direction direction, RelationshipType... rtypes){
		return inner.hasRelationship(direction, rtypes) ;
	}
	
	public ReadRelationship firstRelationShip(Direction direction, RelationshipType rtype){
		return new ReadRelationship(session, inner.getSingleRelationship(rtype, direction)) ;
	}
	

	
	
	
	public String toString(){
		Iterable<String> keysIter = inner.getPropertyKeys();
		Map<String, Object> map = net.ion.framework.util.MapUtil.newMap() ; 
		for (String key : keysIter) {
			map.put(key, inner.getProperty(key)) ;
		}
		return map.toString() ;
	}

	Node inner() {
		return inner ;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof ReadNode){
			return this.inner.equals(((ReadNode) obj).inner) ;
		}
		return false ;
	}
	
	@Override
	public int hashCode(){
		return inner.hashCode() ;
	}

}
