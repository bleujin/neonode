
package net.ion.neo.index.lucene;

import javax.transaction.TransactionManager;

import net.ion.neo.index.impl.lucene.LuceneIndexImplementation;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.index.IndexProviders;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.impl.index.IndexStore;
import org.neo4j.kernel.impl.nioneo.store.FileSystemAbstraction;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.XaFactory;
import org.neo4j.kernel.lifecycle.Lifecycle;

public class LuceneKernelExtensionFactory extends KernelExtensionFactory<LuceneKernelExtensionFactory.Dependencies> {
	public interface Dependencies {
		Config getConfig();

		GraphDatabaseService getDatabase();

		TransactionManager getTxManager();

		XaFactory getXaFactory();

		FileSystemAbstraction getFileSystem();

		XaDataSourceManager getXaDataSourceManager();

		IndexProviders getIndexProviders();

		IndexStore getIndexStore();
	}

	public LuceneKernelExtensionFactory() {
		super(LuceneIndexImplementation.SERVICE_NAME);
	}

	@Override
	public Lifecycle newKernelExtension(Dependencies dependencies) throws Throwable {
		return new LuceneKernelExtension(dependencies.getConfig(), dependencies.getDatabase(), dependencies.getTxManager(), dependencies.getIndexStore(), dependencies.getXaFactory(), dependencies.getFileSystem(), dependencies.getXaDataSourceManager(), dependencies.getIndexProviders());
	}
}
