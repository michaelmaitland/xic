package mtm68.ast.nodes;

public class ArrayLength extends Expr {
	
	private Expr exp;

	public ArrayLength(Expr exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "length [exp=" + exp + "]";
	}
}