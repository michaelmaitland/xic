package mtm68.ir;

import org.junit.jupiter.api.Assertions;

public class TestUtils {

	@SuppressWarnings("unchecked")
	public static <T> T assertInstanceOfAndReturn(Class<T> clazz, Object obj) {
		Assertions.assertTrue(clazz.isAssignableFrom(obj.getClass()), obj.getClass() + " is not an instanceof " + clazz);
		return (T) obj;
	}
}
