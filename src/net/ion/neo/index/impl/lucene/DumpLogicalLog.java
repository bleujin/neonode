package net.ion.neo.index.impl.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.TimeZone;

import org.neo4j.helpers.Pair;
import org.neo4j.kernel.impl.transaction.xaframework.XaCommand;
import org.neo4j.kernel.impl.transaction.xaframework.XaCommandFactory;

public class DumpLogicalLog extends org.neo4j.kernel.impl.util.DumpLogicalLog {
	public static void main(String[] args) throws IOException {
		Pair<Iterable<String>, TimeZone> config = parseConfig(args);
		for (String file : config.first()) {
			int dumped = new DumpLogicalLog().dump(file, config.other());
			if (dumped == 0 && isAGraphDatabaseDirectory(file)) { // If none were found and we really pointed to a neodb directory
				// then go to its index folder and try there.
				new DumpLogicalLog().dump(new File(file, "index").getAbsolutePath(), config.other());
			}
		}
	}

	@Override
	protected XaCommandFactory instantiateCommandFactory() {
		return new CommandFactory();
	}

	@Override
	protected String getLogPrefix() {
		return "lucene.log";
	}

	private static class CommandFactory extends XaCommandFactory {
		@Override
		public XaCommand readCommand(ReadableByteChannel byteChannel, ByteBuffer buffer) throws IOException {
			return LuceneCommand.readCommand(byteChannel, buffer, null);
		}
	}
}
