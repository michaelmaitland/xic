package mtm68.util;

public class FreshTempGenerator {
	private static int counter = 0;
	
	public static int getFreshTemp() {
		return counter++;
	}
}
