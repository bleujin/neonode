package net.ion.neo.util;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.neo.ReadNode;

public abstract class ListIterable<T> implements Iterable<T> {

	public List<T> toList() {
		List<T> result = ListUtil.newList();
		for (T rs : this) {
			result.add(rs) ;
		}
		return result;
	}


	public void debugPrint(Page page) {
		each(page, new DebugPrinter<T>());
	}
	
	public void each(Page page, Closure closure) {
		CollectionUtil.each(toList(page), closure);
	}

	public T first(){
		final Iterator<T> iterator = iterator();
		if (iterator.hasNext()) return iterator.next() ;
		else return null ;
	}
	
	public List<T> toList(Page page) {
		int pageIndexOnScreen = page.getPageNo() - page.getMinPageNoOnScreen() ;
		return toList(pageIndexOnScreen * page.getListNum(), page.getListNum());
	}

	public List<T> toList(int skip, int limit) {
		Iterator<T> iter = this.iterator();
		while (skip-- > 0) {
			if (iter.hasNext()) {
				iter.next();
			} else {
				return ListUtil.EMPTY;
			}
		}
		List<T> result = ListUtil.newList();
		while (limit-- > 0 && iter.hasNext()) {
			result.add(iter.next());
		}
		return result;
	}
}

