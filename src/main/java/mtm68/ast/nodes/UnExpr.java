package mtm68.ast.nodes;

public abstract class UnExpr extends Expr {

	protected Expr expr;
	
	public UnExpr(Expr expr) {
		this.expr = expr;
	}
	
}
