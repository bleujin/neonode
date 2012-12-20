
package net.ion.neo.index.lucene;

import javax.transaction.TransactionManager;

import net.ion.neo.index.impl.lucene.ConnectionBroker;
import net.ion.neo.index.impl.lucene.LuceneDataSource;
import net.ion.neo.index.impl.lucene.LuceneIndexImplementation;
import net.ion.neo.index.impl.lucene.LuceneXaConnection;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseSetting;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexProviders;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.index.IndexConnectionBroker;
import org.neo4j.kernel.impl.index.IndexStore;
import org.neo4j.kernel.impl.index.ReadOnlyIndexConnectionBroker;
import org.neo4j.kernel.impl.nioneo.store.FileSystemAbstraction;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.XaFactory;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;

public class LuceneKernelExtension extends LifecycleAdapter {
	private Config config;
	private GraphDatabaseService gdb;
	private TransactionManager txManager;
	private IndexStore indexStore;
	private XaFactory xaFactory;
	private FileSystemAbstraction fileSystemAbstraction;
	private XaDataSourceManager xaDataSourceManager;
	private IndexProviders indexProviders;

	public static abstract class Configuration {
		public static final GraphDatabaseSetting.BooleanSetting read_only = GraphDatabaseSettings.read_only;
	}

	public LuceneKernelExtension(Config config, GraphDatabaseService gdb, TransactionManager txManager, IndexStore indexStore, XaFactory xaFactory, FileSystemAbstraction fileSystemAbstraction, XaDataSourceManager xaDataSourceManager, IndexProviders indexProviders) {
		this.config = config;
		this.gdb = gdb;
		this.txManager = txManager;
		this.indexStore = indexStore;
		this.xaFactory = xaFactory;
		this.fileSystemAbstraction = fileSystemAbstraction;
		this.xaDataSourceManager = xaDataSourceManager;
		this.indexProviders = indexProviders;
	}

	@Override
	public void start() throws Throwable {
		LuceneDataSource luceneDataSource = new LuceneDataSource(config, indexStore, fileSystemAbstraction, xaFactory);

		xaDataSourceManager.registerDataSource(luceneDataSource);

		IndexConnectionBroker<LuceneXaConnection> broker = config.get(Configuration.read_only) ? new ReadOnlyIndexConnectionBroker<LuceneXaConnection>(txManager) : new ConnectionBroker(txManager, luceneDataSource);

		LuceneIndexImplementation indexImplementation = new LuceneIndexImplementation(gdb, luceneDataSource, broker);
		indexProviders.registerIndexProvider(LuceneIndexImplementation.SERVICE_NAME, indexImplementation);
	}

	@Override
	public void stop() throws Throwable {
		xaDataSourceManager.unregisterDataSource(LuceneDataSource.DEFAULT_NAME);

		indexProviders.unregisterIndexProvider(LuceneIndexImplementation.SERVICE_NAME);
	}
}
