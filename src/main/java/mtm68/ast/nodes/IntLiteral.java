package mtm68.ast.nodes;

public class IntLiteral extends Literal {
	
	private long value;
	
	public IntLiteral(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}

}
