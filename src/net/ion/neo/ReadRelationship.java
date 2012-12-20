package net.ion.neo;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReadRelationship {


	private ReadSession rsession ;
	private Relationship relationShip ;
	ReadRelationship(ReadSession wsession, Relationship relationShip) {
		this.rsession = wsession ;
		this.relationShip = relationShip ;
	}

	public static ReadRelationship load(ReadSession rsession, Relationship relationShip) {
		return new ReadRelationship(rsession, relationShip);
	}

	public ReadNode endNode() {
		return rsession.node(relationShip.getEndNode());
	}

	public ReadNode startNode() {
		return rsession.node(relationShip.getStartNode());
	}

	public boolean has(String pkey){
		return relationShip.hasProperty(pkey) ;
	}
	
	public Object property(String pkey) {
		return relationShip.getProperty(pkey);
	}

	public ReadRelationship property(String pkey, Object value){
		relationShip.setProperty(pkey, value) ;
		return this ;
	}
	
	public Iterable<String> keys(){
		return relationShip.getPropertyKeys() ;
	}

	public RelationshipType type(){
		return relationShip.getType() ;
	}
	
	public boolean isType(RelationshipType type){
		return relationShip.isType(type) ;
	}
	
	
}
