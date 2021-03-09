package mtm68.parser;

import java.util.List;
import java.util.Optional;

import mtm68.ast.nodes.Node;
import mtm68.exception.SyntaxErrorInfo;

public class ParseResult {
	
	private Optional<Node> node;
	private List<SyntaxErrorInfo> syntaxErrors;
	
	public ParseResult(Parser parser) {
		try {
			node = Optional.of((Node)parser.parse().value);
		} catch(Exception e) {
			node = Optional.empty();
		} finally {
			syntaxErrors = parser.getSyntaxErrors();
		}
		sortSyntaxErrors();
	}

	public Optional<Node> getNode() {
		return node;
	}

	public List<SyntaxErrorInfo> getSyntaxErrors() {
		return syntaxErrors;
	}
	
	public boolean isValidAST() {
		return node.isPresent() && syntaxErrors.size() == 0;
	}
	
	public SyntaxErrorInfo getFirstSyntaxError() {
		return syntaxErrors.get(0);
	}
	
	private void sortSyntaxErrors() {
		syntaxErrors.sort((e1, e2) -> {
			return e1.getSymbol().left - e2.getSymbol().left;
		});
	}
}
