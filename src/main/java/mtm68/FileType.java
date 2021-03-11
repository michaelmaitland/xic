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
	
	/** Returns FileType that corresponds to suffix of provided filename.
	 *  Returns null if filename does not end in .xi or .ixi
	 * 
	 * @param filename       filename
	 * @return	             FileType
	 */
	public static FileType parseFileType(String filename) {
		for(FileType ft : FileType.values()) {
			String suffix = "." + ft.name().toLowerCase();
			if(filename.endsWith(suffix)) return ft;
		}
		return null;
	}
}
