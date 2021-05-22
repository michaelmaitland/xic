package mtm68.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	
	public static <T> Set<T> difference(Set<T> one, Set<T> two) {
		return one.stream()
				  .filter(e -> !two.contains(e))
				  .collect(Collectors.toSet());
	}

	public static <V, T extends V> Set<V> fromList(List<T> list) {
		return list.stream().collect(Collectors.toSet());
	}
	
	public static <T> T poll(Set<T> set) {
		Iterator<T> iter = set.iterator();
		T result = iter.next();
		iter.remove();

		return result;
	}

}
