package mtm68.lexer;

import java_cup.runtime.ComplexSymbolFactory.ComplexSymbol;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;
import mtm68.ast.nodes.Node;

public class TokenFactory implements SymbolFactory { 
	
	public Token newToken(TokenType type, int line, int column) {
		return newToken(type, null, line, column);
	}
	
	public Token newToken(TokenType type, Object value, int line, int column) {
		int id = TokenTypeToSymConverter.convert(type);
		String name = type.toString(); 
		Location left = new Location(line, column);
		return new Token(type, name, id, left, value); 
	}

	@Override
	public Symbol newSymbol(String name, int id) {
		return new ComplexSymbol(name, id);
	}

	@Override
	public Symbol newSymbol(String name, int id, Object value) {
		return new ComplexSymbol(name, id, value);
	}

	@Override
	public Symbol newSymbol(String name, int id, Symbol left, Symbol right) {
		return new ComplexSymbol(name, id, left, right);
	}

	@Override
	public Symbol newSymbol(String name, int id, Symbol left, Object value) {
		setLocation(left, value);
		return new ComplexSymbol(name, id, left, value);
	}

	@Override
	public Symbol newSymbol(String name, int id, Symbol left, Symbol right, Object value) {
		setLocation(left, value);
		return new ComplexSymbol(name, id, left, right, value);
	}

	@Override
	public Symbol startSymbol(String name, int id, int state) {
		return new ComplexSymbol(name, id, state);
	}
	
	private void setLocation(Symbol left, Object value) {
		if(!(value instanceof Node)) return;
		Node n = (Node) value;
		ComplexSymbol sym = (ComplexSymbol)left;
		n.setStartLoc(sym.getLeft());
	}
}
