package mtm68.lexer;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;

/**
 * A Token is a <code>ComplexSymbol</code> whose <code>xleft</code> location is not null.
 */
public class Token extends ComplexSymbol {
	
	private TokenType type;
	
	public Token(TokenType type, String name, int id, Location left) {
		super(name, id, left, null);
		assert left != null;
		this.type = type;
	}

	public Token(TokenType type, String name, int id, Location left, Object value) {
		super(name, id, left, null, value);
		assert left != null;
		this.type = type;
	}

	public int getLine() {
		return this.xleft.getLine();
	}

	public int getColumn() {
		return this.xleft.getColumn();
	}
	
	public TokenType getType() {
		return type;
	}
}
