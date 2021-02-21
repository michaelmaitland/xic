package mtm68.util;

import java.nio.file.Files;

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
		String filename = "hello_world.xi";
		String test = "hello world!\n";
		
		String replaced = filename.replaceFirst("\\.[0-9a-zA-Z]+$", ".lexed");
		System.out.println(test.replaceAll("[\n]", "\\\\n"));
	}
}