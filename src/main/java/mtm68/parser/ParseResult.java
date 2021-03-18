package mtm68.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mtm68.ast.nodes.Node;
import mtm68.exception.BaseError;
import mtm68.exception.LexerError;
import mtm68.exception.ParserError;
import mtm68.util.Debug;

public class ParseResult {
	
	private Optional<Node> node;
	private List<BaseError> errors;
	
	public ParseResult(Parser parser) {
		try {
			node = Optional.of((Node)parser.parse().value);
		} catch(Exception e) {
			if(Debug.DEBUG_ON) e.printStackTrace();
			node = Optional.empty();
		} finally {
			errors = getAllErrorsSorted(parser.getLexErrors(), parser.getSyntaxErrors());
		}
	}

	public Optional<Node> getNode() {
		return node;
	}

	public List<ParserError> getSyntaxErrors() {
		return errors.stream()
				.filter(e -> e instanceof ParserError)
				.map(e -> (ParserError)e)
				.collect(Collectors.toList());

	}
	
	public List<LexerError> getLexErrors() {
		return errors.stream()
				.filter(e -> e instanceof LexerError)
				.map(e -> (LexerError)e)
				.collect(Collectors.toList());
	}
	
	private List<BaseError> getAllErrorsSorted(List<LexerError> lexErrors, List<ParserError> syntaxErrors) {
		List<BaseError> errors = new ArrayList<>();
		syntaxErrors.forEach(errors::add);
		lexErrors.forEach(errors::add);
		errors.sort(BaseError.getComparator());
		return errors;
	}
	
	public List<BaseError> getErrors() {
		return errors;
	}
	
	public boolean isValidAST() {
		return node.isPresent() && errors.size() == 0;
	}
	
	public BaseError getFirstError() {
		return errors.get(0);
	}
}
