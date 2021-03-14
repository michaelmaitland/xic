package mtm68.lexer;

import java.math.BigInteger;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.util.StringUtils;

/**
 * A Token is a <code>ComplexSymbol</code> whose <code>xleft</code> location is not null.
 */
public class Token extends ComplexSymbol {
	
	private TokenType type;
	
	public Token(TokenType type, String name, int id, Location left) {
		this(type, name, id, left, null);
	}

	public Token(TokenType type, String name, int id, Location left, Object value) {
		super(name, id, left, left, value);
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if(!toString().equals(other.toString()))
			return false;
		return true;
	}

	private String prettyPrintAttribute(){
        if(value == null) return "";
        
        if(type == TokenType.error) return ":" + value;
        
        if(value instanceof String) {
            String str = (String) value;
            return " " + StringUtils.preserveNewlines(str);
        } else if(value instanceof BigInteger) {
        	BigInteger i = (BigInteger) value;
        	return " " + i.toString();
        } else {
        	return " " + value;
		}
    }

	@Override
	public String toString() {
		return getLine() + ":" + getColumn() + " " + type + prettyPrintAttribute();
	}
}
