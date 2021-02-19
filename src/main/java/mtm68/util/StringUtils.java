package mtm68.util;

public class StringUtils {
	
	public static char convertHexToChar(String hex) {
		String digits = hex.substring(2); // chop off \x
		return (char)Integer.parseInt(digits, 16);
	}
	
	public static void main(String[] args) {
		
		char result = convertHexToChar("\\x0041");
		
		System.out.println("Result: " + result);
		
	}
}
