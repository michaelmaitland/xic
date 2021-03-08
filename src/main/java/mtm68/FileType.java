package mtm68;

import mtm68.lexer.TokenType;

public enum FileType {
	XI(TokenType.XI),
	IXI(TokenType.IXI);
	
	private TokenType tokenType;
	
	private FileType(TokenType tokenType) {
		this.tokenType = tokenType;
	}
	
	public TokenType getTokenType() {
		return tokenType;
	}
	
	public static FileType parseFileType(String filename) {
		for(FileType ft : FileType.values()) {
			String suffix = "." + ft.name().toLowerCase();
			if(filename.endsWith(suffix)) return ft;
		}
		return null;
	}
}
