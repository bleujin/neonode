package net.ion.neo;

import java.util.List;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.common.MyField;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;
import net.ion.neo.index.lucene.QueryContext;

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

public class SessionQuery<T extends NeoNode> {

	private NeoSession session;
	private String queryString;
	private List<SortField> sorts = ListUtil.newList();

	private int limit = 100 ; // default limit
	private int skip = 0 ;
	private int offset = 100 ;
	private boolean tradeForSpeed = false ;
	
	SessionQuery(NeoSession session) {
		this.session = session;
	}

	public static <T> SessionQuery create(NeoSession session) {
		return new SessionQuery<ReadNode>(session);
	}

	public SessionQuery<T> parseQuery(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public SessionQuery<T> ascending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING));
		return this;
	}

	public SessionQuery<T> ascending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType));
		return this;
	}

	public SessionQuery<T> descending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING, true));
		return this;
	}

	public SessionQuery<T> descending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType, true));
		return this;
	}

	public NodeCursor<T> find(){
		try {
			Index<Node> indexer = session.workspace().indexTextNode();
			QueryContext query = createQuery(queryString);

			final IndexHits<Node> hits = indexer.query(query);
			try {
				int _skip = this.skip ;
				int _offset = this.offset ;
				
				List<Node> hitNodes = ListUtil.newList() ;
				while(_skip-- > 0){
					if (hits.hasNext()) {
						hits.next();
					} else {
						NodeCursor.create(session, ListUtil.EMPTY);
						break ;
					}
				}
				
				while (_offset-- > 0 && hits.hasNext()) {
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
	
	public SessionQuery<T> tradeForSpeed(boolean tradeForSpeed){
		this.tradeForSpeed = tradeForSpeed ;
		return this ;
	}
	

	public SessionQuery<T> topDoc(int topDoc){
		this.limit = Math.max(topDoc, 0) ;
		return this ;
	}
	
	public SessionQuery<T> skip(int skip){
		this.skip = Math.max(skip, 0) ;
		return this ;
	}
	
	public SessionQuery<T> offset(int offset){
		this.offset = Math.max(offset, 0) ;
		return this ;
	}
	
	private QueryContext createQuery(String queryString) throws ParseException {
		Query query = null ;
		if (StringUtil.isBlank(queryString)) {
			query = new MatchAllDocsQuery() ;
		} else {
			QueryParser parser = new QueryParser(Version.LUCENE_35, IKeywordField.ISALL_FIELD, new MyKoreanAnalyzer());
			query = parser.parse(queryString);
		}

		QueryContext context = new QueryContext(query).defaultOperator(Operator.AND);
		if (!sorts.isEmpty()) {
			context.sort(new Sort(sorts.toArray(new SortField[0])));
		} else {
			context.sortByScore() ;
		}
		if (tradeForSpeed) context.tradeCorrectnessForSpeed() ;
		context.top(limit) ;

		return context;
	}

	public T findOne() {
		return (T) find().first();
	}

}
