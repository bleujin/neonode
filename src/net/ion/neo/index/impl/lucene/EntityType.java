package net.ion.neo.index.impl.lucene;

import org.apache.lucene.document.Document;
import org.neo4j.graphdb.PropertyContainer;

interface EntityType {
	Document newDocument(Object entityId);

	Class<? extends PropertyContainer> getType();
}
