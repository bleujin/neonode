package net.ion.neo.index.impl.lucene;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.CatchingIteratorWrapper;
import org.neo4j.helpers.collection.IteratorUtil;

public abstract class IdToEntityIterator<T extends PropertyContainer> extends CatchingIteratorWrapper<T, Long> implements IndexHits<T> {
	private final IndexHits<Long> ids;
	private final Set<Long> alreadyReturned = new HashSet<Long>();

	public IdToEntityIterator(IndexHits<Long> ids) {
		super(ids);
		this.ids = ids;
	}

	@Override
	protected boolean exceptionOk(Throwable t) {
		return t instanceof NotFoundException;
	}

	@Override
	protected Long fetchNextOrNullFromSource(Iterator<Long> source) {
		while (source.hasNext()) {
			Long id = source.next();
			if (alreadyReturned.add(id)) {
				return id;
			}
		}
		return null;
	}

	public float currentScore() {
		return this.ids.currentScore();
	}

	public int size() {
		return this.ids.size();
	}

	public Iterator<T> iterator() {
		return this;
	}

	public void close() {
		ids.close();
	}

	public T getSingle() {
		try {
			return IteratorUtil.singleOrNull((Iterator<T>) this);
		} finally {
			close();
		}
	}
}
