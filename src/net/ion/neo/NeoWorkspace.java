package net.ion.neo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.ion.neo.exception.NeoRuntimeException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
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
	private ExecutionEngine engine ;
	private Class<? extends Analyzer> indexAnal ;
	
	private NeoWorkspace(NeoRepository repository, String dbPath, Class<? extends Analyzer> indexAnal) {
		this.repository = repository ;
		this.dbPath = dbPath ; 
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath) ;
		this.engine = new ExecutionEngine(graphDB) ;
		this.indexAnal = indexAnal ;
	}

	public Analyzer newAnalyzer(){
		try {
			return indexAnal.getDeclaredConstructor(Version.class).newInstance(Version.LUCENE_36) ;
		} catch (IllegalArgumentException e) {
			throw NeoRuntimeException.from(e) ;
		} catch (SecurityException e) {
			throw NeoRuntimeException.from(e) ;
		} catch (InstantiationException e) {
			throw NeoRuntimeException.from(e) ;
		} catch (IllegalAccessException e) {
			throw NeoRuntimeException.from(e) ;
		} catch (InvocationTargetException e) {
			throw NeoRuntimeException.from(e) ;
		} catch (NoSuchMethodException e) {
			throw NeoRuntimeException.from(e) ;
		}
	}
	
	public static NeoWorkspace create(NeoRepository repository, String path, Class<? extends Analyzer> indexAnal) {
		return new NeoWorkspace(repository, path, indexAnal);
	}

	public void close(){
		graphDB.shutdown() ;
	}
	
	public void clear() throws IOException {
		graphDB.shutdown() ;
		final File file = new File(dbPath);
		if (file.exists()) FileUtils.deleteRecursively(file);
		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath) ;
		this.engine = new ExecutionEngine(graphDB) ;
	}


	public <T> Future<T> tran(final ReadSession session, final TransactionJob<T> tjob, final TranExceptionHandler handler) {
		final NeoWorkspace workspace = this ;
		return repository.executor().submitTask(new Callable<T>() {

			@Override
			public T call() throws Exception {
				WriteSession tsession = new WriteSession(session, workspace);
				Transaction tran = graphDB.beginTx() ;
				try {
					T result = tjob.handle(tsession) ;
					tran.success() ;
					return result ;
				} catch(Throwable ex) {
					tran.failure() ;
					handler.handle(tsession, ex) ;
				} finally {
					tran.finish() ;
				}
				return null;
			}
			
		}) ;
	}
	
	private Index<Node> indexKeyNode(){
		return graphDB.index().forNodes("keyproperty", MapUtil.stringMap(IndexManager.PROVIDER, "neolucene", "type", "exact", "to_lower_case", "true")) ;
	}
	
	public RelationshipIndex indexTextRelation(){
		return graphDB.index().forRelationships("fulltextrelation", MapUtil.stringMap(IndexManager.PROVIDER, "neolucene", "type", "fulltext", "to_lower_case", "true", "analyzer", indexAnal.getCanonicalName())) ; //MyKoreanAnalyzer.class.getCanonicalName()
	}
	
	public Index<Node> indexTextNode(){
		
//		return graphDB.index().forNodes("keyproperty", MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "exact", "to_lower_case", "true")) ;
		return graphDB.index().forNodes("fulltextproperty", MapUtil.stringMap(IndexManager.PROVIDER, "neolucene", "type", "fulltext", "to_lower_case", "true", "analyzer", indexAnal.getCanonicalName())) ;
	}
	
	
	// use only test
	GraphDatabaseService graphDB() {
		return graphDB ;
	}
	
	Node getNodeById(long id){
		return graphDB.getNodeById(id) ;
	}
	
	ExecutionEngine executionEngine(){
		return engine ;
	}
	
	Node createNode(){
		return graphDB.createNode() ;
	}
	
	
	public NeoWorkspace registerEventHandler(KernelEventHandler ehandler){
		graphDB.registerKernelEventHandler(ehandler) ;
		return this ;
	}
	
	public <T> NeoWorkspace registerTransactionHandler(TransactionEventHandler<T> ehandler){
		graphDB.registerTransactionEventHandler(ehandler) ;
		return this ;
	}
	
	
}
