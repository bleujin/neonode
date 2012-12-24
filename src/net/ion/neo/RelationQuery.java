package net.ion.neo;

import java.util.List;

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
import org.neo4j.graphdb.index.RelationshipIndex;

public class RelationQuery<T extends NeoRelationship> {

	private NeoSession session ;
	private String queryString;
	private List<SortField> sorts = ListUtil.newList();
	
	public RelationQuery(NeoSession session) {
		this.session = session ;
	}

	public static RelationQuery create(NeoSession session) {
		return new RelationQuery(session);
	}


	public RelationQuery<T> parseQuery(String queryString) {
		this.queryString = queryString;
		return this;
	}

	public RelationQuery<T> ascending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING));
		return this;
	}

	public RelationQuery<T> ascending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType));
		return this;
	}

	public RelationQuery<T> descending(String propId) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, SortField.STRING, true));
		return this;
	}

	public RelationQuery<T> descending(String propId, int sortType) {
		sorts.add(new SortField(propId + MyField.SORT_POSTFIX, sortType, true));
		return this;
	}

	public RelationCursor<T> find(){
		try {
			
			
			RelationshipIndex indexer = session.workspace().indexTextRelation();
			QueryContext query = createQuery(queryString);

			return RelationCursor.create(session, indexer.query(query));
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex);
		}
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
		}

		return context;
	}

	public T findOne() {
		return (T) find().first();
	}
}
