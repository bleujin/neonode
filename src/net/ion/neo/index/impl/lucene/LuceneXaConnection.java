package net.ion.neo.index.impl.lucene;

import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.kernel.impl.index.IndexXaConnection;
import org.neo4j.kernel.impl.transaction.xaframework.XaConnectionHelpImpl;
import org.neo4j.kernel.impl.transaction.xaframework.XaResourceHelpImpl;
import org.neo4j.kernel.impl.transaction.xaframework.XaResourceManager;

/**
 * An XA connection used with {@link LuceneDataSource}. This class is public because the XA framework requires it.
 */
public class LuceneXaConnection extends XaConnectionHelpImpl implements IndexXaConnection {
	private final LuceneXaResource xaResource;

	LuceneXaConnection(Object identifier, XaResourceManager xaRm, byte[] branchId) {
		super(xaRm);
		xaResource = new LuceneXaResource(identifier, xaRm, branchId);
	}

	@Override
	public XAResource getXaResource() {
		return xaResource;
	}

	private static class LuceneXaResource extends XaResourceHelpImpl {
		private final Object identifier;

		LuceneXaResource(Object identifier, XaResourceManager xaRm, byte[] branchId) {
			super(xaRm, branchId);
			this.identifier = identifier;
		}

		@Override
		public boolean isSameRM(XAResource xares) {
			if (xares instanceof LuceneXaResource) {
				return identifier.equals(((LuceneXaResource) xares).identifier);
			}
			return false;
		}
	}

	private LuceneTransaction luceneTx;

	LuceneTransaction getLuceneTx() {
		if (luceneTx == null) {
			try {
				luceneTx = (LuceneTransaction) getTransaction();
			} catch (XAException e) {
				throw new RuntimeException("Unable to get lucene tx", e);
			}
		}
		return luceneTx;
	}

	<T extends PropertyContainer> void add(LuceneIndex<T> index, T entity, String key, Object value) {
		getLuceneTx().add(index, entity, key, value);
	}

	<T extends PropertyContainer> void remove(LuceneIndex<T> index, T entity, String key, Object value) {
		getLuceneTx().remove(index, entity, key, value);
	}

	<T extends PropertyContainer> void remove(LuceneIndex<T> index, T entity, String key) {
		getLuceneTx().remove(index, entity, key);
	}

	<T extends PropertyContainer> void remove(LuceneIndex<T> index, T entity) {
		getLuceneTx().remove(index, entity);
	}

	<T extends PropertyContainer> void deleteIndex(LuceneIndex<T> index) {
		getLuceneTx().delete(index);
	}

	public void createIndex(Class<? extends PropertyContainer> entityType, String name, Map<String, String> config) {
		getLuceneTx().createIndex(entityType, name, config);
	}
}
