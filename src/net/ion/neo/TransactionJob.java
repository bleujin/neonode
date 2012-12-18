package net.ion.neo;

public interface TransactionJob<T> {

	public T handle(WriteSession tsession) ;
}
