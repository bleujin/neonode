package net.ion.neo.util;

import java.util.List;

public interface ListIterable<T> extends Iterable<T> {

	public List<T> toList() ;
}
