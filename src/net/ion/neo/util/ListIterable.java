package net.ion.neo.util;

import java.util.Iterator;
import java.util.List;

import net.ion.framework.db.Page;
import net.ion.framework.util.Closure;
import net.ion.framework.util.CollectionUtil;
import net.ion.framework.util.ListUtil;

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
	
	public void each(Closure<T> closure) {
		each(Page.ALL, closure) ;
	}
	public void each(Page page, Closure<T> closure) {
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

	public List<T> toList(int skip, int length) {
		Iterator<T> iter = this.iterator();
		while (skip-- > 0) {
			if (iter.hasNext()) {
				iter.next();
			} else {
				return ListUtil.EMPTY;
			}
		}
		List<T> result = ListUtil.newList();
		while (length-- > 0 && iter.hasNext()) {
			result.add(iter.next());
		}
		return result;
	}
}

