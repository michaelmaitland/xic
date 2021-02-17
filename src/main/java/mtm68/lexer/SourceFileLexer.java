package mtm68.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import mtm68.lexer.Lexer.Token;

public class SourceFileLexer {

	private Lexer lexer;
	
	private Reader in;

	public SourceFileLexer(String filename) throws FileNotFoundException {
		this.in = new FileReader(filename);
		this.lexer = new Lexer(in);
	}

	public Token nextToken() throws java.io.IOException {
		return lexer.nextToken();
	}
}
