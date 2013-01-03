package net.ion.neo.tquery;

import net.ion.framework.util.Debug;
import net.ion.neo.NeoPath;
import net.ion.neo.NeoTraverser;
import net.ion.neo.ReadNode;
import net.ion.neo.ReadRelationship;
import net.ion.neo.TestNeoNodeBase;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteNode;
import net.ion.neo.WriteRelationship;
import net.ion.neo.WriteSession;
import net.ion.neo.NeoWorkspace.RelType;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;

public class TestFirstTraverse extends TestNeoNodeBase {

	public void setUp() throws Exception {
		super.setUp() ;
		session.dropWorkspace() ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				
				WriteNode root = tsession.rootNode();
				root.property("name", "root") ;
				
				WriteNode bleujin = root.mergeRelationNode(RelType.CHILD, "bleujin").property("name", "bleujin").property("age", 20).property("text", "태극기가 바람에 펄럭입니다");
				WriteNode hero = root.mergeRelationNode(RelType.CHILD, "hero").property("name", "hero").property("age", 25).propertyWithoutIndex("noindex", 3);
				
				bleujin.createRelationshipTo(hero, RelType.CHILD).property("type", "friend") ;
				return null;
			}
		}).get() ;
	}

	
	public void testTraverse() throws Exception {
		TraversalDescription td = Traversal.description().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());
		
		NeoTraverser<NeoPath<ReadNode, ReadRelationship>> nt = session.rootNode().traverse(td);
		for (NeoPath<ReadNode, ReadRelationship> neoPath : nt) {
			Debug.line(neoPath.staratNode(), neoPath.endNode()) ;
		} 
	}
	
	public void testWriteTraverse() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				TraversalDescription td = Traversal.description().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition());
				NeoTraverser<NeoPath<WriteNode, WriteRelationship>> nt = tsession.rootNode().traverse(td);
				
				for (NeoPath<WriteNode, WriteRelationship> neoPath : nt) {
					neoPath.relationships().first().property("mod", "modRel") ;
				} 
				return null;
			}
		}).get() ;

		TraversalDescription td = Traversal.description().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.toDepth(1));
		
		NeoTraverser<NeoPath<ReadNode, ReadRelationship>> nt = session.rootNode().traverse(td);
		for (NeoPath<ReadNode, ReadRelationship> neoPath : nt) {
			Debug.line(neoPath.staratNode(), neoPath.endNode(), neoPath.relationships().first()) ;
		} 

	}
	
	
	
}
