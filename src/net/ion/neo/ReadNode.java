package net.ion.neo;

import java.util.Iterator;

import net.ion.neo.util.ListIterable;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.TraversalDescription;

public class ReadNode extends NeoNode{

	
	private ReadSession session;

	private ReadNode(ReadSession session, Node inner){
		super(inner) ;
		this.session = session ;
	}
	
	public static ReadNode load(ReadSession session, Node inner) {
		return new ReadNode(session, inner);
	}

	public ListIterable<ReadRelationship> relationShips(Direction direction, RelationshipType... rtypes){
		if (rtypes.length == 0){
			return new IterableReadRelation(session, inner().getRelationships(direction)) ;
		}
		return new IterableReadRelation(session, inner().getRelationships(direction, rtypes)) ;
	}
	
	
	public ReadRelationship firstRelationShip(Direction direction, RelationshipType... rtypes){
		Iterator<Relationship> rels = null ;
		if (rtypes.length == 0){
			rels = inner().getRelationships(direction).iterator() ;
		} else {
			rels = inner().getRelationships(direction, rtypes).iterator();
		}
		if (rels.hasNext()) {
			return new ReadRelationship(session, rels.next()) ;
		} else return null ;
	}
	
	public NeoTraverser traverse(TraversalDescription td) {
		return NeoTraverser.create(session, td.traverse(inner())) ;
	}

}
