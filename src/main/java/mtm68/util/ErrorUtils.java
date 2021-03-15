package mtm68.util;

import mtm68.parser.ParseResult;

public class ErrorUtils {
	
	public static void printErrors(ParseResult result, String filename) {
		result.getErrors()
			.stream()
			.map(e -> e.getPrintErrorMessage(filename))
			.forEach(System.out::println);
	}

}
