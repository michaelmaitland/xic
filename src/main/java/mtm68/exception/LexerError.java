package mtm68.exception;

import mtm68.lexer.Token;

public class LexerError extends BaseError {
	
	private Token token;

	public LexerError(Token token) {
		super(ErrorKind.LEXICAL, token.getLine(), token.getColumn());
		this.token = token;
	}

	@Override
	public String getDescription() {
		return token.value.toString();
	}

	@Override
	public String getFileErrorMessage() {
		return token.getLine() + ":" + token.getColumn() + " error:" + getDescription();
	}

}
