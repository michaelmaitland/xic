package mtm68.util;

public class Debug {

	public static boolean DEBUG_ON = false;
	
	private static String method;
	private static long startTime;
	
	public static void startTime(String method) {
		Debug.method = method;
		startTime = System.nanoTime();
	}
	
	public static void endTime() {
		double diff = (System.nanoTime() - startTime) / 1_000_000;
		System.out.println(method + " took " + diff + "ms to run");
	}
}
