package mtm68.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
	
	public static <T> List<T> singleton(T elem) {
		List<T> ret = new ArrayList<T>(); 
		ret.add(elem);
		return ret;
	}

	public static <T> List<T> elems(T... elems) {
		List<T> ret = new ArrayList<T>(); 
		for(T elem : elems) {
			ret.add(elem);
		}
		return ret;
	}
	
	public static <T> List<T> concat(List<T> one, List<T> two) {
		one.addAll(two);
		return one;
	}

	public static <T> List<T> append(List<T> list, T elem) {
		list.add(elem);
		return list;
	}

	public static <T> List<T> empty() {
		return new ArrayList<T>();
	}

}
