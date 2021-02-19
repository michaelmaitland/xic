package mtm68.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

import mtm68.lexer.Lexer.Token;

public class SourceFileLexer {

	private String filename;
	
	private Reader in;

	private Lexer lexer;

	public SourceFileLexer(String filename) throws FileNotFoundException {
		this.filename = filename;
		this.in = new FileReader(filename);
		this.lexer = new Lexer(in);
	}

	public Token nextToken() throws java.io.IOException {
		return lexer.nextToken();
	}
	
	public List<Token> getTokens() throws java.io.IOException {
		// TODO: get all tokens and return
		return null;
	}
	
	public int getLineNum() {
		return -1;
		//return lexer.zzline();
	}
	
	public String getFilename() {
		return filename;
	}
}
