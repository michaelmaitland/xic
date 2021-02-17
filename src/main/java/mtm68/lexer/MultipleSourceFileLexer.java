package mtm68.lexer;

import java.io.IOException;
import java.util.List;

import mtm68.lexer.Lexer.Token;

public class MultipleSourceFileLexer {

	private List<String> filenames;

	private SourceFileLexer lexer;

	public MultipleSourceFileLexer(List<String> filenames) throws IOException {
		if (filenames == null || filenames.size() == 0) {
			throw new IOException(
					"Cannot construct MultipleSourceFileLexer because "
					+ "\"filenames\" must contain at least one element");
		}
		this.filenames = filenames;
		this.lexer = new SourceFileLexer(popFilename());
	}

	public Token nextToken() throws java.io.IOException {
		Token t = lexer.nextToken();
		if (t == null && filenames.size() == 0) { // no more tokens
			return null;
		} else if (t == null && filenames.size() > 0) { // time for new file
			// TODO: close old lexer?
			lexer = new SourceFileLexer(popFilename());
			return nextToken();
		} else { // current file had nextToken
			return t;
		}
	}

	/*
	 * Removes a single element from the filenames list. 
	 * Precondition: filenames.size() > 0
	 */
	private String popFilename() {
		assert filenames.size() > 0;
		String filename = filenames.get(0);
		filenames.remove(0);
		return filename;
	}

	public int getLineNum() {
		return lexer.getLineNum();
	}

	public String getFilename() {
		return lexer.getFilename();
	}
}
