package mtm68.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import mtm68.lexer.Lexer.Token;
import mtm68.lexer.Lexer.TokenType;

public class SourceFileLexer {

	private String filename;

	private Lexer lexer;

	private List<Token> tokens;

	public SourceFileLexer(String filename) throws FileNotFoundException {
		this.filename = filename;
		this.lexer = new Lexer(new FileReader(filename));
	}

	public SourceFileLexer(String filename, Reader reader) {
		this.filename = filename;
		this.lexer = new Lexer(reader);
	}

	// TODO: Throw LexerException
	/**
	 * Returns list of tokens lexed from the file on which the SourceFileLexer was instantiated on.
	 * 
	 * @return list of tokens
	 * @throws IOException
	 */
	public List<Token> getTokens() throws IOException {
		if (tokens == null) {
			tokens = new ArrayList<>();

			for (Token token = lexer.nextToken(); token != null; token = lexer.nextToken()) {
				tokens.add(token);
				if (token.getType() == TokenType.ERROR)
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
