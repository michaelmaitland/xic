package mtm68.ast.types;

public enum Result {
	VOID,
	UNIT;
	
	public static Result leastUpperBound(Result r1, Result r2) {
		if(r1 == VOID && r2 == VOID) return VOID;
		return UNIT;
	}
}
