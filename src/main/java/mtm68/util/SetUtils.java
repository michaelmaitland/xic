package mtm68.util;

import java.util.HashSet;
import java.util.Set;

public class SetUtils {
	
	@SafeVarargs
	public static <T> Set<T> elems(T...elems) {
		Set<T> result = new HashSet<>();
		for(T elem : elems) result.add(elem);
		return result;
	}

	public static <T> Set<T> empty() {
		return new HashSet<>();
	}
	
	public static <T> Set<T> copy(Set<T> original) {
		return new HashSet<T>(original);
	}

}
