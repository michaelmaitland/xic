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
}