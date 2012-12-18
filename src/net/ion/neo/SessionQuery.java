package net.ion.neo;

import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.isearcher.common.IKeywordField;
import net.ion.isearcher.searcher.MyKoreanAnalyzer;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;

public class SessionQuery {

	private NeoWorkspace workspace ;
	private NeoSession session ;
	private String queryString ;
	
	private List<SortField> sorts = ListUtil.newList() ;
	SessionQuery(NeoWorkspace workspace, NeoSession session) {
		this.workspace = workspace ;
		this.session = session ;
	}

	public static SessionQuery create(NeoWorkspace workspace, NeoSession session) {
		return new SessionQuery(workspace, session);
	}

	public SessionQuery parseQuery(String queryString) throws ParseException {
		this.queryString = queryString ;
		return this;
	}
	
	public SessionQuery ascending(String propId){
		sorts.add(new SortField(propId, SortField.STRING)) ;
		return this ;
	}

	public SessionQuery ascending(String propId, int sortType){
		sorts.add(new SortField(propId, sortType)) ;
		return this ;
	}

	public SessionQuery descending(String propId){
		sorts.add(new SortField(propId, SortField.STRING, true)) ;
		return this ;
	}

	public SessionQuery descending(String propId, int sortType){
		sorts.add(new SortField(propId, sortType, true)) ;
		return this ;
	}

	public NodeCursor find() throws ParseException{
		Index<Node> indexer = workspace.indexTextNode();
		QueryParser parser = new QueryParser(Version.LUCENE_35, IKeywordField.ISALL_FIELD, new MyKoreanAnalyzer());
		Query query = parser.parse(queryString);
		
		QueryContext context = new QueryContext(query).defaultOperator(Operator.AND) ;
		if (! sorts.isEmpty()){
			context.sort(new Sort(sorts.toArray(new SortField[0]))) ;
		}
		
		return NodeCursor.create(session, indexer.query(context));

	}
	
}
