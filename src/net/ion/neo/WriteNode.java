package net.ion.neo;

import java.util.Iterator;

import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class WriteNode {

	private WriteSession wsession ;
	private Node inner ;
	
	WriteNode(WriteSession tsession, Node inner) {
		this.wsession = tsession ;
		this.inner = inner ;
	}

	public long getId(){
		return inner.getId() ;
	}
	
	public static WriteNode findBy(WriteSession tsession, Node inner) {
		return new WriteNode(tsession, inner);
	}

	public WriteNode property(String pkey, Object value) {
		wsession.workspace().indexTextNode().add(inner, pkey, value) ;
		inner.setProperty(pkey, value) ;
		return this ;
	}

	public WriteNode propertyWithoutIndex(String pkey, Object value) {
		inner.setProperty(pkey, value) ;
		return this ;
	}
	

	public WriteNode remove(String pkey){
		inner.removeProperty(pkey) ;
		return this ;
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
	
	public ListIterable<WriteRelationship> relationShips(Direction direction){
		return new WriteIterableRealtion(wsession, inner.getRelationships(direction)) ;
	}
	
	public ListIterable<WriteRelationship> relationShips(Direction direction, RelationshipType rtype){
		return new WriteIterableRealtion(wsession, inner.getRelationships(direction, rtype)) ;
	}
	
	public ListIterable<WriteRelationship> relationShips(Direction direction, RelationshipType... rtypes){
		return new WriteIterableRealtion(wsession, inner.getRelationships(direction, rtypes)) ;
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
	
	public WriteRelationship firstRelationShip(Direction direction, RelationshipType... rtypes){
		Iterator<WriteRelationship> iterator = relationShips(direction, rtypes).iterator();
		if (iterator.hasNext()){
			return iterator.next() ; 
		}
		return null ; 
	}

	
	public WriteRelationship createRelationshipTo(WriteNode node, RelationshipType rel) {
		Relationship relationShip = inner.createRelationshipTo(node.inner, rel);
		return new WriteRelationship(wsession, relationShip);
	}

	
	
	public WriteNode mergeChild(String relName) {
		return wsession.mergeChildNode(this, relName) ;
	}

	Node inner() {
		return inner;
	}




}
