package net.ion.neo;

import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class ReadRelationship extends NeoRelationship {


	private ReadSession rsession ;
	ReadRelationship(ReadSession wsession, Relationship relationShip) {
		super(relationShip) ;
		this.rsession = wsession ;
	}

	public static ReadRelationship load(ReadSession rsession, Relationship relationShip) {
		return new ReadRelationship(rsession, relationShip);
	}

	public ReadNode endNode() {
		return rsession.node(relationShip().getEndNode());
	}

	public ReadNode startNode() {
		return rsession.node(relationShip().getStartNode());
	}

	public ReadRelationship property(String pkey, Object value){
		relationShip().setProperty(pkey, value) ;
		return this ;
	}
}
