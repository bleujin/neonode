package net.ion.neo;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class WriteRelationship {

	private WriteSession wsession ;
	private Relationship relationShip ;
	WriteRelationship(WriteSession wsession, Relationship relationShip) {
		this.wsession = wsession ;
		this.relationShip = relationShip ;
	}

	public static WriteRelationship load(WriteSession wsession, Relationship relationShip) {
		return new WriteRelationship(wsession, relationShip);
	}

	public WriteNode endNode() {
		return WriteNode.findBy(wsession, relationShip.getEndNode());
	}

	public WriteNode startNode() {
		return WriteNode.findBy(wsession, relationShip.getStartNode());
	}

	public boolean has(String pkey){
		return relationShip.hasProperty(pkey) ;
	}
	
	public Object property(String pkey) {
		return relationShip.getProperty(pkey);
	}

	public WriteRelationship property(String pkey, Object value){
		relationShip.setProperty(pkey, value) ;
		return this ;
	}
	
	public Iterable<String> keys(){
		return relationShip.getPropertyKeys() ;
	}

	public WriteRelationship remove(String pkey){
		relationShip.removeProperty(pkey) ;
		return this ;
	}
	

	public RelationshipType type(){
		return relationShip.getType() ;
	}
	
	public boolean isType(RelationshipType type){
		return relationShip.isType(type) ;
	}
	
	
	
	
	
	
}
