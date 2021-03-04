package mtm68.ast.nodes;

public class BinExpr extends Expr {
	
	private Binop op;
	private Expr left;
	private Expr right;

	public BinExpr(Binop op, Expr left, Expr right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
}
