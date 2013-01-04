package net.ion.neo;

import net.ion.framework.util.ListUtil;
import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class WriteNode extends NeoNode {

	private WriteSession wsession ;

	WriteNode(WriteSession tsession, Node inner) {
		super(inner) ;
		this.wsession = tsession ;
	}

	public static WriteNode load(WriteSession tsession, Node inner) {
		return new WriteNode(tsession, inner);
	}

	public WriteNode property(String pkey, Object value) {
		wsession.workspace().indexTextNode().add(inner(), pkey, value) ;
		inner().setProperty(pkey, value) ;
		return this ;
	}

	public WriteNode propertyWithoutIndex(String pkey, Object value) {
		inner().setProperty(pkey, value) ;
		return this ;
	}

	public WriteNode unset(String pkey){
		inner().removeProperty(pkey) ;
		return this ;
	}
	
	public ListIterable<WriteRelationship> relationShips(Direction direction, RelationshipType... rtypes){
		if (rtypes.length == 0){
			return new IterableWriteRelation(wsession, inner().getRelationships(direction)) ;
		}
		return new IterableWriteRelation(wsession, inner().getRelationships(direction, rtypes)) ;
	}

	public WriteRelationship firstRelationShip(Direction direction, RelationshipType... rtypes){
		return relationShips(direction, rtypes).first() ;
	}

	public WriteRelationship createRelationshipTo(WriteNode node, RelationshipType rel) {
		return createRelationshipTo(node, rel, NeoConstant.DefaultRelationName) ;
	}

	public WriteRelationship createRelationshipTo(WriteNode node, RelationshipType rel, String relName) {
		Relationship relationShip = inner().createRelationshipTo(node.inner(), rel);
		return new WriteRelationship(wsession, relationShip).property(NeoConstant.RelationName, relName);
	}

	public WriteNode mergeRelationNode(RelationshipType rtype, String relName) {
		return wsession.mergeRelationNode(this, rtype, relName) ;
	}

	public WriteNode createRelationNode(RelationshipType rtype, String relName) {
		return wsession.createRelationNode(this, rtype, relName) ;
	}



	public void remove() {
		final Node node = inner();
		wsession.workspace().indexTextNode().remove(node) ;
		for (Relationship rel : node.getRelationships()) {
			rel.delete() ;
		} 
		node.delete() ;
	}

	public NeoTraversalDescription<WriteNode, WriteRelationship> traversal() {
		return NeoTraversalDescription.create(ListUtil.toList(this), wsession) ;
	}

	

}
