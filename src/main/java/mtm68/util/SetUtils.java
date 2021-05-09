package mtm68.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public static <T> Set<T> union(Set<T> one, Set<T> two) {
		return Stream.concat(one.stream(), two.stream())
				.collect(Collectors.toSet());
	}

	public static <T> void unionMutable(Set<T> one, Set<T> two) {
		one.addAll(two);
	}

	public static <T> Set<T> intersect(Set<T> one, Set<T> two) {
		return one.stream()
				.filter(two::contains)
				.collect(Collectors.toSet());
	}
}
