package mtm68.lexer;

import mtm68.parser.*;
public class TokenTypeToSymConverter {

	public static int convert(TokenType type) {
		String name = type.name();
		for (int i=0; i < sym.terminalNames.length; i++) {
			if(sym.terminalNames[i].equals(name)) {
				return i;
			}
		}
		throw new RuntimeException("TokenType " + type.toString() 
		+ " could not be converted to sym. Make sure xi.cup has same types as TokenType.");
	}
}
