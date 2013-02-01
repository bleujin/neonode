package net.ion.neo;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.neo.index.lucene.QueryContext;
import net.ion.nsearcher.common.IKeywordField;
import net.ion.nsearcher.common.MyField;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

public class SessionQuery<T extends NeoNode, R extends NeoRelationship> {

	private NeoSession<T, R> session;
	private String queryString;
	private List<SortField> sorts = ListUtil.newList();

	private int topDoc = 100 ; // default limit
	private int skip = 0 ;
	private int atLength = 100 ;
	private boolean tradeForSpeed = false ;
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
	
	SessionQuery(NeoSession<T, R> session) {
		this.session = session;
	}

	static <T extends NeoNode, R extends NeoRelationship> SessionQuery<T, R> create(NeoSession<T, R> session) {
		return new SessionQuery<T, R>(session);
	}

	public SessionQuery<T, R> parseQuery(String queryString) {
		this.queryString = queryString;
		return this;
	}
	
	public SessionQuery<T, R> analyzer(Analyzer analyzer) {
		this.analyzer = analyzer ;
		return this;
	}



	public SessionQuery<T, R> ascending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING));
		return this;
	}

	public SessionQuery<T, R> ascending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType));
		return this;
	}

	public SessionQuery<T, R> descending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING, true));
		return this;
	}

	public SessionQuery<T, R> descending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType, true));
		return this;
	}

	public NodeCursor<T, R> find(){
		try {
			Index<Node> indexer = session.workspace().indexTextNode();
			QueryContext query = createQuery(queryString);

			final IndexHits<Node> hits = indexer.query(query);
			try {
				int _skip = this.skip ;
				int _limit = this.atLength ;
				
				List<Node> hitNodes = ListUtil.newList() ;
				while(_skip-- > 0){
					if (hits.hasNext()) {
						hits.next();
					} else {
						break ;
					}
				}
				
				while (_limit-- > 0 && hits.hasNext()) {
					hitNodes.add(hits.next());
				}
				
				return NodeCursor.create(session, hitNodes);
			} finally {
				if (hits != null) hits.close() ;
			} 
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	public SessionQuery<T, R> tradeForSpeed(boolean tradeForSpeed){
		this.tradeForSpeed = tradeForSpeed ;
		return this ;
	}
	

	public SessionQuery<T, R> topDoc(int topDoc){
		this.topDoc = Math.max(topDoc, 0) ;
		return this ;
	}
	
	public SessionQuery<T, R> skip(int skip){
		this.skip = Math.max(skip, 0) ;
		return this ;
	}
	
	public SessionQuery<T, R> atLength(int limit){
		this.atLength = Math.max(limit, 0) ;
		return this ;
	}
	
	private QueryContext createQuery(String queryString) throws ParseException {
		Query query = null ;
		if (StringUtil.isBlank(queryString)) {
			query = new MatchAllDocsQuery() ;
		} else {
			QueryParser parser = new QueryParser(Version.LUCENE_35, IKeywordField.ISALL_FIELD, analyzer);
			query = parser.parse(queryString);
		}

		QueryContext context = new QueryContext(query).defaultOperator(Operator.AND);
		if (!sorts.isEmpty()) {
			context.sort(new Sort(sorts.toArray(new SortField[0])));
		} else {
			context.sortByScore() ;
		}
		if (tradeForSpeed) context.tradeCorrectnessForSpeed() ;
		context.top(topDoc) ;

		return context;
	}

	public T findOne() {
		return (T) find().first();
	}

	public NeoSession<T, R> getSession() {
		return session;
	}

	public NeoTraversalDescription<T, R> traversal() {
		return NeoTraversalDescription.create(this);
	}

}
