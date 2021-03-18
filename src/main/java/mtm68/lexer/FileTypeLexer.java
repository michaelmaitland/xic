package mtm68.lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

import java_cup.runtime.Symbol;
import mtm68.FileType;

public class FileTypeLexer extends Lexer {
	
	private boolean firstToken;
	private FileType fileType;
	private TokenFactory tokenFactory;
	
	public FileTypeLexer(String filename, Path sourcePath, FileType fileType, TokenFactory tokenFactory) throws FileNotFoundException {
		this(new FileReader(sourcePath.resolve(filename).toString()), fileType, tokenFactory);
	}

	public FileTypeLexer(Reader in, FileType fileType, TokenFactory tokenFactory) {
		super(in, tokenFactory);
		this.fileType = fileType;
		this.tokenFactory = tokenFactory; 
		firstToken = true;
	}
	
	@Override
	public Symbol next_token() throws IOException {
		if(firstToken) {
			firstToken = false;
			return tokenFactory.newToken(fileType.getTokenType(), 1, 1);
		}

		return super.next_token();
	}

}
