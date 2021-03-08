package mtm68.lexer;

import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;
import mtm68.FileType;

public class FileTypeLexer extends Lexer {
	
	private boolean firstToken;
	private FileType fileType;
	private TokenFactory tokenFactory;

	public FileTypeLexer(Reader in, FileType fileType) {
		super(in);
		this.fileType = fileType;
		firstToken = true;
		tokenFactory = new TokenFactory();
	}
	
	@Override
	public Symbol next_token() throws IOException {
		if(firstToken) {
			firstToken = false;
			return tokenFactory.newToken(fileType.getTokenType(), 0, 0);
		}

		return super.next_token();
	}

}
