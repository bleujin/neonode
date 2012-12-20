package net.ion.neo.index.lucene;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.TransactionManager;

import net.ion.neo.index.impl.lucene.ConnectionBroker;
import net.ion.neo.index.impl.lucene.LuceneDataSource;
import net.ion.neo.index.impl.lucene.LuceneIndexImplementation;
import net.ion.neo.index.impl.lucene.LuceneXaConnection;

import org.neo4j.graphdb.DependencyResolver;
import org.neo4j.graphdb.factory.GraphDatabaseSetting;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexImplementation;
import org.neo4j.graphdb.index.IndexProvider;
import org.neo4j.kernel.InternalAbstractGraphDatabase;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.impl.index.IndexConnectionBroker;
import org.neo4j.kernel.impl.index.IndexStore;
import org.neo4j.kernel.impl.index.ReadOnlyIndexConnectionBroker;
import org.neo4j.kernel.impl.nioneo.store.FileSystemAbstraction;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.XaFactory;

public class LuceneIndexProvider extends IndexProvider {
	private static List<WeakReference<LuceneIndexImplementation>> previousProviders = new ArrayList<WeakReference<LuceneIndexImplementation>>();

	public static abstract class Configuration {
		public static final GraphDatabaseSetting<Boolean> read_only = GraphDatabaseSettings.read_only;
	}

	public LuceneIndexProvider() {
		super(LuceneIndexImplementation.SERVICE_NAME);
	}

	@Override
	public IndexImplementation load(DependencyResolver dependencyResolver) {
		Config config = dependencyResolver.resolveDependency(Config.class);
		InternalAbstractGraphDatabase gdb = dependencyResolver.resolveDependency(InternalAbstractGraphDatabase.class);
		TransactionManager txManager = dependencyResolver.resolveDependency(TransactionManager.class);
		IndexStore indexStore = dependencyResolver.resolveDependency(IndexStore.class);
		XaFactory xaFactory = dependencyResolver.resolveDependency(XaFactory.class);
		FileSystemAbstraction fileSystemAbstraction = dependencyResolver.resolveDependency(FileSystemAbstraction.class);
		XaDataSourceManager xaDataSourceManager = dependencyResolver.resolveDependency(XaDataSourceManager.class);

		LuceneDataSource luceneDataSource = new LuceneDataSource(config, indexStore, fileSystemAbstraction, xaFactory);

		xaDataSourceManager.registerDataSource(luceneDataSource);

		IndexConnectionBroker<LuceneXaConnection> broker = config.get(Configuration.read_only) ? new ReadOnlyIndexConnectionBroker<LuceneXaConnection>(txManager) : new ConnectionBroker(txManager, luceneDataSource);

		// TODO This is a hack to support reload of HA instances. Remove if HA supports start/stop of single instance instead
		for (Iterator<WeakReference<LuceneIndexImplementation>> iterator = previousProviders.iterator(); iterator.hasNext();) {
			WeakReference<LuceneIndexImplementation> previousProvider = iterator.next();
			LuceneIndexImplementation indexImplementation = previousProvider.get();
			if (indexImplementation == null)
				iterator.remove();
			else if (indexImplementation.matches(gdb))
				indexImplementation.reset(luceneDataSource, broker);
		}

		LuceneIndexImplementation indexImplementation = new LuceneIndexImplementation(gdb, luceneDataSource, broker);
		previousProviders.add(new WeakReference<LuceneIndexImplementation>(indexImplementation));
		return indexImplementation;
	}

}
