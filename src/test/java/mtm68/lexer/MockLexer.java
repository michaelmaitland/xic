package mtm68.lexer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import java_cup.runtime.Symbol;

public class MockLexer extends Lexer {
	
	private Iterator<Token> tokenIterator;

	public MockLexer(List<Token> tokens) {
		super(null);
		
		tokenIterator = tokens.iterator();
	}
	
	@Override
	public Symbol next_token() throws IOException {
		if(tokenIterator.hasNext()) {
			return tokenIterator.next();
		}
		return null;
	}

}
