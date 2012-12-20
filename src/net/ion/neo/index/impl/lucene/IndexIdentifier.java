package net.ion.neo.index.impl.lucene;

class IndexIdentifier {
	final String indexName;
	final EntityType entityType;
	final byte entityTypeByte;
	private final int hashCode;

	public IndexIdentifier(byte entityTypeByte, EntityType entityType, String indexName) {
		this.entityTypeByte = entityTypeByte;
		this.entityType = entityType;
		this.indexName = indexName;
		this.hashCode = calculateHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !getClass().equals(o.getClass())) {
			return false;
		}
		IndexIdentifier i = (IndexIdentifier) o;
		return entityTypeByte == i.entityTypeByte && indexName.equals(i.indexName);
	}

	private int calculateHashCode() {
		int code = 17;
		code += 7 * entityTypeByte;
		code += 7 * indexName.hashCode();
		return code;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public String toString() {
		return "Index[" + indexName + "," + (entityTypeByte == LuceneCommand.NODE ? "Node" : "Relationship") + "]";
	}
}
