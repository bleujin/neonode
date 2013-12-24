package net.ion.neo.bleujin;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.bean.ResultSetHandler;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleCacheReleaseDBManager;
import net.ion.framework.db.servant.AfterTask;
import net.ion.framework.db.servant.IExtraServant;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.neo.TransactionJob;
import net.ion.neo.WriteSession;
import net.ion.neo.bleujin.TestBase.RelTypes;

public class TestFromDB extends TestCase {

	private DBController dc;
	private final String dbPath = "./resource/db";
	protected GraphDatabaseService graphDB;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DBManager dbm = new OracleCacheReleaseDBManager("jdbc:oracle:thin:@61.250.201.239:1521:qm10g", "ibr_15843", "ibr_15843", 10);
		this.dc = new DBController("testDc", dbm, new IExtraServant() {
			@Override
			public void support(AfterTask task) {
				Debug.line(task.getQueryable());
			}
		});
		dc.initSelf();
		clearDb();

		this.graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdonwHook(graphDB);
	}

	@Override
	protected void tearDown() throws Exception {
		IOUtil.closeQuietly(dc);
		super.tearDown();
	}

	private void clearDb() {
		try {
			FileUtils.deleteRecursively(new File(dbPath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void registerShutdonwHook(final GraphDatabaseService graphDB) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (graphDB != null)
					graphDB.shutdown();
			}
		});
	}

	

	public static RelationshipType createType(final String rel){
		return new RelationshipType() {
			public String name() {
				return rel;
			}
		} ;	
	}
	
	// 120 sec
	public void testAfield() throws Exception {
		final long start = System.currentTimeMillis();
		int count = dc.createUserCommand("select artId, modSerNo, afieldId, typeCd, dValue, hashValue from afield_content_tblc").execHandlerQuery(new ResultSetHandler<Integer>() {
			@Override
			public Integer handle(final ResultSet rs) throws SQLException {
				try {
					Debug.line(System.currentTimeMillis() - start);
					int result = 0;
					Transaction tx = graphDB.beginTx();
					
					try {
						Node root = graphDB.getNodeById(0);
						Relationship toRoot = root.getSingleRelationship(createType("articles"), Direction.INCOMING);
						if (toRoot == null){
							Node newNode = graphDB.createNode();
							toRoot = newNode.createRelationshipTo(root, createType("articles")) ;
						}
						Node articles = toRoot.getStartNode() ;
						
						while (rs.next()) {
							Relationship toArticles = articles.getSingleRelationship(createType(rs.getString("artId")), Direction.INCOMING);
							if (toArticles == null){
								Node newNode = graphDB.createNode() ;
								toArticles = newNode.createRelationshipTo(articles, createType(rs.getString("artId"))) ;
							}
							Node anode = toArticles.getStartNode();
							
							anode.setProperty("modserno", rs.getInt("modserno")) ;
							anode.setProperty("type", rs.getString("typeCD")) ;
							anode.setProperty(rs.getString("afieldId"), rs.getString("dvalue")) ;
							result++;
							
							if ((result % 15001) == 0) {
								tx.success() ;
								tx.finish() ;
								tx = graphDB.beginTx() ;
							}
						}
						tx.success();
					} catch(SQLException ex){
						tx.failure() ;
						throw ex ;
					} finally {
						tx.finish();
					}
					return result;

				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
		});

		Debug.line(count, System.currentTimeMillis() - start);
	}
	

	// 120 sec
	public void testAfieldReverse() throws Exception {
		final long start = System.currentTimeMillis();
		int count = dc.createUserCommand("select artId, modSerNo, afieldId, typeCd, dValue, hashValue from afield_content_tblc").execHandlerQuery(new ResultSetHandler<Integer>() {
			@Override
			public Integer handle(final ResultSet rs) throws SQLException {
				try {
					Debug.line(System.currentTimeMillis() - start);
					int result = 0;
					Transaction tx = graphDB.beginTx();
					
					
					
					try {
						Node root = graphDB.getNodeById(0);
						Relationship toArticles = root.getSingleRelationship(createType("articles"), Direction.OUTGOING);
						if (toArticles == null){
							Node newNode = graphDB.createNode();
							toArticles = root.createRelationshipTo(newNode, createType("articles")) ;
						}
						Node articles = toArticles.getEndNode() ;
						
						while (rs.next()) {
							Relationship toArticle = articles.getSingleRelationship(createType(rs.getString("artId")), Direction.OUTGOING);
							if (toArticle == null){
								Node newNode = graphDB.createNode() ;
								toArticle = articles.createRelationshipTo(newNode, createType(rs.getString("artId"))) ;
							}
							Node anode = toArticles.getEndNode();
							
							anode.setProperty("modserno", rs.getInt("modserno")) ;
							anode.setProperty("type", rs.getString("typeCd")) ;
							anode.setProperty(rs.getString("afieldId"), rs.getString("dvalue")) ;
							result++;
							
							if ((result % 15001) == 0) {
								tx.success() ;
								tx.finish() ;
								tx = graphDB.beginTx() ;
							}
						}
						tx.success();
					} catch(SQLException ex){
						tx.failure() ;
						throw ex ;
					} finally {
						tx.finish();
					}
					return result;

				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
		});

		Debug.line(count, System.currentTimeMillis() - start);
	}
	
	
	
	// 35 sec
	public void testArticle() throws Exception {
		final long start = System.currentTimeMillis();
		int count = dc.createUserCommand("select catId, artId, modSerNo, artSubject, modUserId, artCont from article_tblc where isUsing = 'T'").execHandlerQuery(new ResultSetHandler<Integer>() {
			@Override
			public Integer handle(final ResultSet rs) throws SQLException {
				try {
					Debug.line(System.currentTimeMillis() - start);
					int result = 0;
					Transaction tx = graphDB.beginTx();
					try {
						while (rs.next()) {
							Node newNode = graphDB.createNode();
							newNode.setProperty("id", "/articles/" + rs.getString("artId") + "/" + rs.getString("modSerNo")) ;
							newNode.setProperty("subject", rs.getString("artSubject")) ;
							newNode.setProperty("modUserId", rs.getString("modUserId"));
							newNode.setProperty("artcont", rs.getString("artCont"));
							result++;
							
							if ((result % 15001) == 0) {
								tx.success() ;
								tx.finish() ;
								tx = graphDB.beginTx() ;
							}
						}
						tx.success();
					} catch(SQLException ex){
						tx.failure() ;
						throw ex ;
					} finally {
						tx.finish();
					}
					return result;

				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
		});

		Debug.line(count, System.currentTimeMillis() - start);
	}

	
}
