package net.ion.neo;

import org.neo4j.graphdb.Relationship;

public class WriteRelationship extends NeoRelationship<WriteNode> {

	private WriteSession wsession ;
	WriteRelationship(WriteSession wsession, Relationship relationShip) {
		super(relationShip) ;
		this.wsession = wsession ;
	}

	public static WriteRelationship load(WriteSession wsession, Relationship relationShip) {
		return new WriteRelationship(wsession, relationShip);
	}

	public WriteNode endNode() {
		return wsession.node(relationShip().getEndNode());
	}

	public WriteNode startNode() {
		return wsession.node(relationShip().getStartNode());
	}

	public WriteRelationship property(String pkey, Object value){
		wsession.workspace().indexTextRelation().add(relationShip(), pkey, value) ;
		relationShip().setProperty(pkey, value) ;
		return this ;
	}
	
	public WriteRelationship unset(String pkey){
		relationShip().removeProperty(pkey) ;
		return this ;
	}
	
	public void remove(){
		relationShip().delete() ;
	}
	
}
