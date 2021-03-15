package mtm68.exception;

import mtm68.lexer.Token;

public class ParserError extends BaseError {
	private Token token;

	public ParserError(Token token) {
		super(ErrorKind.SYNTAX, token.getLine(), token.getColumn());
		this.token = token;
	}
	
	@Override
	public String getDescription() {
		return "Unexpected token " + token.getName();
	}

	@Override
	public String getFileErrorMessage() {
		return token.getLine() + ":" + token.getColumn() + " error:" + getDescription();
	}
	
	public Token getToken() {
		return token;
	}
}
