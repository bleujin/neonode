package net.ion.neo;

import java.util.Comparator;
import java.util.List;

import net.ion.framework.util.ListUtil;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.BranchOrderingPolicy;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.InitialBranchState;
import org.neo4j.graphdb.traversal.PathEvaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.UniquenessFactory;
import org.neo4j.kernel.Traversal;

public class NeoTraversalDescription<T extends NeoNode, R extends NeoRelationship> {

	private List<T> resultNode ;
	private TraversalDescription td;
	private NeoSession<T, R> session ;
	
	private NeoTraversalDescription(List<T> resultNode, NeoSession<T, R> session) {
		this.resultNode = resultNode ;
		this.session = session ; 
		this.td = Traversal.description() ;
	}
	
	public final static <T extends NeoNode, R extends NeoRelationship> NeoTraversalDescription<T, R> create(SessionQuery<T, R> sessionQuery){
		List<T> list = sessionQuery.find().toList(); 
		NeoSession<T, R> session = sessionQuery.getSession() ;
		return create(list, session) ;
	}
	
	public final static <T extends NeoNode, R extends NeoRelationship> NeoTraversalDescription<T, R> create(List<T> list, NeoSession<T, R> session){
		return new NeoTraversalDescription<T, R>(list, session) ;
	}
	
	
    public NeoTraversalDescription<T, R> uniqueness(UniquenessFactory uniquenessfactory){
    	td = td.uniqueness(uniquenessfactory) ;
    	return this ;
    }

    public NeoTraversalDescription<T, R> uniqueness(UniquenessFactory uniquenessfactory, Object obj){
    	td = td.uniqueness(uniquenessfactory, obj) ;
    	return this ;
    }
    public NeoTraversalDescription<T, R> evaluator(Evaluator evaluator){
    	td = td.evaluator(evaluator) ;
    	return this ;
    }
    public NeoTraversalDescription<T, R> evaluator(PathEvaluator pathevaluator){
    	td = td.evaluator(pathevaluator) ;
    	return this ;
    }
    public NeoTraversalDescription<T, R> order(BranchOrderingPolicy branchorderingpolicy){
    	td = td.order(branchorderingpolicy) ;
    	return this ;
    }
    public NeoTraversalDescription<T, R> depthFirst(){
    	td = td.depthFirst() ;
    	return this ;
    }
    public NeoTraversalDescription<T, R> breadthFirst(){
    	td = td.breadthFirst() ;
    	return this ;
    }
    public NeoTraversalDescription<T, R> relationships(RelationshipType relationshiptype){
    	td = td.relationships(relationshiptype) ;
    	return this ;
    }

    public NeoTraversalDescription<T, R> relationships(RelationshipType relationshiptype, Direction direction){
    	td = td.relationships(relationshiptype, direction) ;
    	return this ;
    }

    public NeoTraversalDescription<T, R> expand(PathExpander<?> pathexpander){
    	td = td.expand(pathexpander) ;
    	return this ;
    }

    public NeoTraversalDescription<T, R> expand(PathExpander pathexpander, InitialBranchState initialbranchstate){
    	td = td.expand(pathexpander, initialbranchstate) ;
    	return this ;
    }

    public NeoTraversalDescription<T, R> sort(Comparator<? super Path> comparator){
    	td = td.sort(comparator) ;
    	return this ;
    }

    public NeoTraversalDescription<T, R> reverse(){
    	td.reverse() ;
    	return this ;
    }

    public NeoTraverser<T, R> traverse(){
    	List<Node> result = ListUtil.newList() ;
    	for (T t : resultNode) {
			result.add(t.inner()) ;
		}
    	final Traverser traverse = td.traverse(result.toArray(new Node[0]));

    	if (session instanceof ReadSession){
    		return NeoTraverser.create((ReadSession)session, traverse) ;
    	} else {
    		return NeoTraverser.create((WriteSession)session, traverse) ;
    	}
    }
	
	
}
