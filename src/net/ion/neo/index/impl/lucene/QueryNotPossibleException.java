package net.ion.neo.index.impl.lucene;

/**
 * Querying an index inside a transaction where modifications has been made must be explicitly enabled using QueryContext.allowQueryingModifications
 */
public class QueryNotPossibleException extends RuntimeException {
	public QueryNotPossibleException() {
		super();
	}

	public QueryNotPossibleException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryNotPossibleException(String message) {
		super(message);
	}

	public QueryNotPossibleException(Throwable cause) {
		super(cause);
	}
}
