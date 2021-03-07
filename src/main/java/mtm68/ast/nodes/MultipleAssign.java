package mtm68.ast.nodes;

public class MultipleAssign extends Assign {
	
	private FExpr rhs;
	
	public MultipleAssign(FExpr rhs) {
		this.rhs = rhs;
	}

}
