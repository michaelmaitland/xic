package mtm68.ast.nodes;

public abstract class UnExpr extends Expr {

	protected Expr expr;
	
	public UnExpr(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}
}
