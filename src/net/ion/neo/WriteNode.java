package net.ion.neo;

import java.util.Iterator;

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

	public static WriteNode findBy(WriteSession tsession, Node inner) {
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
	
	public ListIterable<WriteRelationship> relationShips(Direction direction){
		return new IterableWriteRealtion(wsession, inner().getRelationships(direction)) ;
	}
	
	public ListIterable<WriteRelationship> relationShips(Direction direction, RelationshipType rtype){
		return new IterableWriteRealtion(wsession, inner().getRelationships(direction, rtype)) ;
	}
	
	public ListIterable<WriteRelationship> relationShips(Direction direction, RelationshipType... rtypes){
		return new IterableWriteRealtion(wsession, inner().getRelationships(direction, rtypes)) ;
	}

	public WriteRelationship firstRelationShip(Direction direction, RelationshipType... rtypes){
		Iterator<WriteRelationship> iterator = relationShips(direction, rtypes).iterator();
		if (iterator.hasNext()){
			return iterator.next() ; 
		}
		return null ; 
	}
	
	public WriteRelationship createRelationshipTo(WriteNode node, RelationshipType rel) {
		Relationship relationShip = inner().createRelationshipTo(node.inner(), rel);
		return new WriteRelationship(wsession, relationShip);
	}

	public WriteNode mergeChild(String relName) {
		return wsession.mergeChildNode(this, relName) ;
	}

	public void remove() {
		final Node node = inner();
		wsession.workspace().indexTextNode().remove(node) ;
		for (Relationship rel : node.getRelationships()) {
			rel.delete() ;
		} 
		node.delete() ;
	}



}
