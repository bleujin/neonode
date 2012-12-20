package net.ion.neo.index.impl.lucene;

import javax.transaction.TransactionManager;

import org.neo4j.kernel.impl.index.IndexConnectionBroker;

public class ConnectionBroker extends IndexConnectionBroker<LuceneXaConnection> {
	private final LuceneDataSource xaDs;

	public ConnectionBroker(TransactionManager transactionManager, LuceneDataSource dataSource) {
		super(transactionManager);
		this.xaDs = dataSource;
	}

	@Override
	protected LuceneXaConnection newConnection() {
		return (LuceneXaConnection) xaDs.getXaConnection();
	}
}
