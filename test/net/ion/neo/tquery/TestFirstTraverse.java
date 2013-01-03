package net.ion.neo.tquery;

import net.ion.framework.util.Debug;
import net.ion.neo.NeoPath;
import net.ion.neo.NeoTraversalDescription;
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
		super.setUp();
		session.dropWorkspace();

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {

				WriteNode root = tsession.rootNode();
				root.property("name", "root");

				WriteNode bleujin = root.mergeRelationNode(RelType.CHILD, "bleujin").property("name", "bleujin").property("age", 20).property("text", "태극기가 바람에 펄럭입니다");
				WriteNode hero = root.mergeRelationNode(RelType.CHILD, "hero").property("name", "hero").property("age", 25).propertyWithoutIndex("noindex", 3);

				bleujin.createRelationshipTo(hero, RelType.CHILD).property("type", "friend");
				return null;
			}
		}).get();
	}

	public void testQueryTraverse() throws Exception {
		NeoTraverser<NeoPath<ReadNode, ReadRelationship>> nt = session.createQuery().parseQuery("name:root").traversal().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition()).traverse();
		for (NeoPath<ReadNode, ReadRelationship> neoPath : nt) {
			Debug.line(neoPath.startNode(), neoPath.endNode());
		}

		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				NeoTraverser<NeoPath<WriteNode, WriteRelationship>> wnt = tsession.createQuery().parseQuery("name:root").traversal().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition()).traverse();
				for (NeoPath<WriteNode, WriteRelationship> neoPath : wnt) {
					Debug.line(neoPath.startNode(), neoPath.endNode());
				}

				return null;
			}
		}).get();

		
		NeoTraverser<NeoPath<ReadNode, ReadRelationship>> listnt = NeoTraversalDescription.create(session.createQuery().parseQuery("name:root").find().toList(), session).breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition()).traverse();
		
	}
	
	

	public void testTraverse() throws Exception {
		NeoTraverser<NeoPath<ReadNode, ReadRelationship>> nt = session.rootNode().traversal().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition()).traverse();
		for (NeoPath<ReadNode, ReadRelationship> neoPath : nt) {
			Debug.line(neoPath.startNode(), neoPath.endNode());
		}
	}

	public void testWriteTraverse() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {

				NeoTraverser<NeoPath<WriteNode, WriteRelationship>> nt = tsession.rootNode().traversal().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.excludeStartPosition()).traverse() ;

				for (NeoPath<WriteNode, WriteRelationship> neoPath : nt) {
					neoPath.relationships().first().property("mod", "modRel");
				}
				return null;
			}
		}).get();

		long start = System.currentTimeMillis();
		NeoTraverser<NeoPath<ReadNode, ReadRelationship>> nt = session.rootNode().traversal().breadthFirst().relationships(RelType.CHILD, Direction.OUTGOING).evaluator(Evaluators.toDepth(1)).traverse() ;
		for (NeoPath<ReadNode, ReadRelationship> neoPath : nt) {
			Debug.line(neoPath.startNode(), neoPath.endNode(), neoPath.relationships().first());
		}

		Debug.line(System.currentTimeMillis() - start);
	}

}
