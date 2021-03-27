package mtm68.util;

import java.util.List;

import mtm68.exception.BaseError;
import mtm68.parser.ParseResult;

public class ErrorUtils {
	
	public static void printErrors(ParseResult result, String filename) {
		printErrors(result.getErrors(), filename);
	}

	public static void printErrors(List<BaseError> errors, String filename) {
		errors.stream()
		.map(e -> e.getPrintErrorMessage(filename))
		.forEach(System.out::println);		
	}
}
