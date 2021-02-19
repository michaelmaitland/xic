package mtm68.util;

public class StringUtils {
	
	public static char convertHexToChar(String hex) {
		String digits = hex.substring(2); // chop off \x
		return (char)Integer.parseInt(digits, 16);
	}

	public static long convertHexToLong(String hex) {
		String digits = hex.substring(2); // chop off \x
		return Long.parseLong(digits, 16);
	}
	
	public static void main(String[] args) {
		
		char result = convertHexToChar("\\x0041");
		
		String sub = "'\\x0041'".replace("'", "");
		
		System.out.println("Result: " + result);
		System.out.println("Test: " + (long)'x');
		System.out.println("Long: " + convertHexToLong(sub));
	}
}
