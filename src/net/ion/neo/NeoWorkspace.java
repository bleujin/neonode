package net.ion.neo;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.ion.isearcher.searcher.MyKoreanAnalyzer;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.impl.util.FileUtils;

public class NeoWorkspace {

	public enum RelType implements RelationshipType {
		CHILD ; 
		
		public static RelationshipType create(final String name){
			return new RelationshipType() {
				@Override
				public String name() {
					return name;
				}
			};
		}
	} 
	
	private final NeoRepository repository ;
	private final String dbPath ;
	private GraphDatabaseService graphDB ;
	private NeoWorkspace(NeoRepository repository, String dbPath) {
		this.repository = repository ;
		this.dbPath = dbPath ; 
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath) ;
	}

	public static NeoWorkspace create(NeoRepository repository, String path) {
		return new NeoWorkspace(repository, path);
	}

	public void close(){
		graphDB.shutdown() ;
	}
	
	public void clear() throws IOException {
		graphDB.shutdown() ;
		final File file = new File(dbPath);
		if (file.exists()) FileUtils.deleteRecursively(file);
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath) ;
	}


	public <T> Future<T> tran(final NeoSession session, final TransactionJob<T> tjob) {
		final NeoWorkspace workspace = this ;
		return repository.executor().submitTask(new Callable<T>() {

			@Override
			public T call() throws Exception {
				WriteSession tsession = new WriteSession(session, workspace, graphDB);
				Transaction tran = graphDB.beginTx() ;
				try {
					tjob.handle(tsession) ;
					
					
					tran.success() ;
				} catch(Throwable ex) {
					tran.failure() ;
				} finally {
					tran.finish() ;
				}
				return null;
			}
			
		}) ;
	}
	
	private Index<Node> indexKeyNode(){
		return graphDB.index().forNodes("keyproperty", MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "exact", "to_lower_case", "true")) ;
	}
	
	public Index<Node> indexTextNode(){
		
		return graphDB.index().forNodes("keyproperty", MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "exact", "to_lower_case", "true")) ;
		
//		return graphDB.index().forNodes("fulltextproperty", MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext", "to_lower_case", "true", "analyzer", MyKoreanAnalyzer.class.getCanonicalName())) ;
	}
	
	
	
	public ReadNode rootNode(NeoSession session) {
		return ReadNode.findBy(session, graphDB.getNodeById(0));
	}
	
	public WriteNode rootNode(WriteSession tsession){
		return WriteNode.findBy(tsession, graphDB.getNodeById(0)) ;
	}
	
}
