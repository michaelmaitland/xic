package mtm68.exception;

import mtm68.lexer.Token;

public class SyntaxErrorInfo {
	private Token token;
	private String message;
	
	public SyntaxErrorInfo(Token token) {
		this.token = token;
	}
	
	public Token getToken() {
		return token;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Syntax error: Unexpected token \"" + token.getType() + "\" at location " + token.getLine() + ":" + token.getColumn();
	}
	
	public String toFileString() {
		if(symbol instanceof Token) {
			Token token = (Token) symbol;
			return token.getLine() + ":" + token.getColumn() + " error:Unexpected token " + token.getName();
		} else {
			return "Syntax error: Unexpected symbol " + symbol;
		}
	}
}
