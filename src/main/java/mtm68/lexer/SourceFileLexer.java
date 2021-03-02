package mtm68.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java_cup.sym;
import java_cup.runtime.Symbol;


public class SourceFileLexer {

	private String filename;

	private Lexer lexer;

	private List<Symbol> tokens;

	//TODO test
	public SourceFileLexer(String filename, Path sourcePath) throws FileNotFoundException {
		this.filename = filename;
		this.lexer = new Lexer(new FileReader(sourcePath.resolve(filename).toString()));
	}
	
	public SourceFileLexer(String filename) throws FileNotFoundException {
		this.filename = filename;
		this.lexer = new Lexer(new FileReader(filename));
	}

	public SourceFileLexer(String filename, Reader reader) {
		this.filename = filename;
		this.lexer = new Lexer(reader);
	}

	/**
	 * Returns list of tokens lexed from the file on which the SourceFileLexer was instantiated on.
	 * 
	 * @return list of tokens
	 * @throws IOException
	 */
	public List<Symbol> getTokens() throws IOException {
		if (tokens == null) {
			tokens = new ArrayList<>();

			for (Symbol token = lexer.next_token(); token != null; token = lexer.next_token()) {
				tokens.add(token);
				if (token.sym == sym.error)
					break;
			}
			lexer.yyclose();
		}
		return tokens;
	}

	public String getFilename() {
		return filename;
	}
}
