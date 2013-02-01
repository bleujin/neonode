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
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;

public class SessionRelationshipQuery<R extends NeoRelationship> {

	private NeoSession<?, R> session ;
	private String queryString;
	private List<SortField> sorts = ListUtil.newList();
	
	private int topDoc = 100 ; // default limit
	private int skip = 0 ;
	private int atLength = 100 ;
	private boolean tradeForSpeed = false ;
	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36) ;

	public SessionRelationshipQuery(NeoSession<?, R> session) {
		this.session = session ;
	}

	public static <T extends NeoNode, R extends NeoRelationship>  SessionRelationshipQuery<R> create(NeoSession<T, R> session) {
		return new SessionRelationshipQuery<R>(session);
	}


	public SessionRelationshipQuery<R> parseQuery(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public SessionRelationshipQuery<R> analyzer(Analyzer analyzer) {
		this.analyzer = analyzer ;
		return this;
	}

	
	public SessionRelationshipQuery<R> ascending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING));
		return this;
	}

	public SessionRelationshipQuery<R> ascending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType));
		return this;
	}

	public SessionRelationshipQuery<R> descending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING, true));
		return this;
	}

	public SessionRelationshipQuery<R> descending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType, true));
		return this;
	}
	
	public RelationshipCursor<R> find(){
		try {
			RelationshipIndex indexer = session.workspace().indexTextRelation();
			QueryContext query = createQuery(queryString);

			final IndexHits<Relationship> hits = indexer.query(query);
			
			try {
				int _skip = this.skip ;
				int _atLength = this.atLength ;
				
				List<Relationship> hitNodes = ListUtil.newList() ;
				while(_skip-- > 0){
					if (hits.hasNext()) {
						hits.next();
					} else {
						break ;
					}
				}
				
				while (_atLength-- > 0 && hits.hasNext()) {
					hitNodes.add(hits.next());
				}
				
				return RelationshipCursor.create(session, hitNodes);
			} finally {
				if (hits != null) hits.close() ;
			}
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex);
		}
	}
	
	public SessionRelationshipQuery<R> tradeForSpeed(boolean tradeForSpeed){
		this.tradeForSpeed = tradeForSpeed ;
		return this ;
	}
	public SessionRelationshipQuery<R> topDoc(int topDoc){
		this.topDoc = Math.max(topDoc, 0) ;
		return this ;
	}
	
	public SessionRelationshipQuery<R> skip(int skip){
		this.skip = Math.max(skip, 0) ;
		return this ;
	}
	
	public SessionRelationshipQuery<R> atLength(int atLength){
		this.topDoc = Math.max(atLength, 0) ;
		return this ;
	}
	
	private QueryContext createQuery(String queryString) throws ParseException {
		Query query = null ;
		if (StringUtil.isBlank(queryString)) {
			query = new MatchAllDocsQuery() ;
		} else {
			QueryParser parser = new QueryParser(Version.LUCENE_36, IKeywordField.ISALL_FIELD, analyzer);
			query = parser.parse(queryString);
//			Debug.line(queryString, query, analyzer) ; 
		}

		QueryContext context = new QueryContext(query).defaultOperator(Operator.AND);
		if (!sorts.isEmpty()) {
			context.sort(new Sort(sorts.toArray(new SortField[0])));
		} else {
			context.sortByScore() ;
		}
		context.top(topDoc) ;

		return context;
	}	
	public R findOne() {
		return (R) find().first();
	}
}
