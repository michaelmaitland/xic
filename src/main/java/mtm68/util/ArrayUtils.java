package mtm68.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayUtils {
	
	/** 
	 *  Returns list composed of single argument
	 */
	public static <T> List<T> singleton(T elem) {
		List<T> ret = new ArrayList<T>(); 
		ret.add(elem);
		return ret;
	}
	
	/** 
	 *  Returns list composed of all arguments
	 */
	@SafeVarargs
	public static <T> List<T> elems(T... elems) {
		List<T> ret = new ArrayList<T>(); 
		for(T elem : elems) {
			ret.add(elem);
		}
		return ret;
	}

	/** 
	 *  Returns concatenation of the two passed lists
	 */
	public static <T> List<T> concat(List<T> one, List<T> two) {
		one.addAll(two);
		return one;
	}

	/** 
	 *  Returns list with elem appended
	 */
	public static <T> List<T> append(List<T> list, T elem) {
		list.add(elem);
		return list;
	}
	
	/** 
	 *  Returns list with elem prepended
	 */
	public static <T> List<T> prepend(T elem, List<T> list) {
		list.add(0, elem);
		return list;
	}

	/** 
	 *  Returns empty list
	 */
	public static <T> List<T> empty() {
		return new ArrayList<T>();
	}

	/** 
	 *  Returns set composed of all arguments
	 */
	@SuppressWarnings("unchecked")
	public static final <T> Set<T> newHashSet(T... objs) {
	    Set<T> set = new HashSet<T>();
	    Collections.addAll(set, objs);
	    return set;
	}
}
