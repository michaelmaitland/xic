package mtm68.util;

import java.util.ArrayList;
import java.util.List;

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
	 *  Returns empty list
	 */
	public static <T> List<T> empty() {
		return new ArrayList<T>();
	}

}
