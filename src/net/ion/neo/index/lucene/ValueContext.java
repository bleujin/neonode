package net.ion.neo.index.lucene;

import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.Index;

/**
 * ValueContext allows you to give not just a value, but to give the value some context to live in.
 */
public class ValueContext {
	private final Object value;
	private boolean indexNumeric;

	public ValueContext(Object value) {
		this.value = value;
	}

	/**
	 * @return the value object specified in the constructor.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns a ValueContext to be used with {@link Index#add(PropertyContainer, String, Object)}
	 * 
	 * @return a numeric ValueContext
	 */
	public ValueContext indexNumeric() {
		if (!(this.value instanceof Number)) {
			throw new IllegalStateException("Value should be a Number, is " + value + " (" + value.getClass() + ")");
		}
		this.indexNumeric = true;
		return this;
	}

	/**
	 * Returns the string representation of the value given in the constructor, or the unmodified value if {@link #indexNumeric()} has been called.
	 * 
	 * @return the, by the user, intended value to index.
	 */
	public Object getCorrectValue() {
		return this.indexNumeric ? this.value : this.value.toString();
	}

	@Override
	public String toString() {
		return value.toString();
	}

	/**
	 * Convience method to add a numeric value to an index.
	 * 
	 * @param value
	 *            The value to add
	 * @return A ValueContext that can be used with {@link Index#add(PropertyContainer, String, Object)}
	 */
	public static ValueContext numeric(Number value) {
		return new ValueContext(value).indexNumeric();
	}
}
