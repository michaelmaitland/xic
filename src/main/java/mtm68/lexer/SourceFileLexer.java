package mtm68.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
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
	
	public List<Token> getTokens() throws IOException {
		List<Token> ret = new ArrayList<>();
		for(Token token = lexer.nextToken(); token != null;) {
			ret.add(token);
		}
		return ret;
	}

	public String getFilename() {
		return filename;
	}
}
