package net.ion.neo;


import org.neo4j.graphdb.Relationship;

public class ReadRelationship extends NeoRelationship<ReadNode> {

	private ReadSession rsession;

	ReadRelationship(ReadSession wsession, Relationship relationShip) {
		super(relationShip);
		this.rsession = wsession;
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

	public ReadRelationship property(String pkey, Object value) {
		relationShip().setProperty(pkey, value);
		return this;
	}
}
