package mtm68.lexer;

import java_cup.runtime.ComplexSymbolFactory.Location;

public class TokenFactory  {
	
	public Token newToken(TokenType type, int line, int column) {
		return newToken(type, null, line, column);
	}
	
	public Token newToken(TokenType type, Object value, int line, int column) {
		int id = TokenTypeToSymConverter.convert(type);
		String name = type.toString(); 
		Location left = new Location(line, column);
		return new Token(type, name, id, left, value); 
	}
}
