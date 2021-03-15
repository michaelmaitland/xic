package mtm68.exception;

import java.util.Comparator;

public abstract class BaseError {
	
	private ErrorKind kind;
	private int line;
	private int col;

	public BaseError(ErrorKind kind, int line, int col) {
		this.kind = kind;
		this.line = line;
		this.col = col;
	}

	public abstract String getDescription();
	public abstract String getFileErrorMessage();

	public String getPrintErrorMessage(String filename) {
		return kind + " error beginning at " + filename + ":" + line + ":" + col + ": " + getDescription();
	}
	
	public static Comparator<BaseError> getComparator() {
		return (e1, e2) -> {
			int lineDiff = e1.getLine() - e2.getLine();
			if(lineDiff == 0) {
				return e1.getCol() - e2.getCol();
			}
			return lineDiff;
		};
	}
	
	public int getLine() {
		return line;
	}
	
	public int getCol() {
		return col;
	}
	
	public ErrorKind getKind() {
		return kind;
	}
	
	public enum ErrorKind {
		LEXICAL,
		SYNTAX,
		SEMANTIC;
		
		@Override
		public String toString() {
			char firstLetter = name().charAt(0);
			String rest = name().substring(1).toLowerCase();
			return firstLetter + rest;
		}
	}

}
