package mtm68.exception;

import java_cup.runtime.Symbol;
import mtm68.lexer.Token;

public class SyntaxErrorInfo {
	private Symbol symbol;
	private String message;
	
	public SyntaxErrorInfo(Symbol symbol) {
		this.symbol = symbol;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		if(symbol instanceof Token) {
			Token token = (Token) symbol;
			return "Syntax error: Unexpected token \"" + token.getType() + "\" at location " + token.getLine() + ":" + token.getColumn();
		} else {
			return "Syntax error: Unexpected symbol " + symbol;
		}
	}
}
