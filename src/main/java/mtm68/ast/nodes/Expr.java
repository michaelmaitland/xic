package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRExpr;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Type;

public abstract class Expr extends Node implements HasType {

	protected Type type;
	
	protected IRExpr irExpr;
	
	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public IRExpr getIrExpr() {
		return irExpr;
	}

	public void setIrExpr(IRExpr irExpr) {
		this.irExpr = irExpr;
	}

	/**
	 * Copies this Expr, sets the type of the copied Expr,
	 *  and returns that copied Expr.
	 * @param type the type to set the copied Expr
	 * @return the copied Expr
	 */
	public <E extends Expr> E copyAndSetType(Type type) {
		E newE = this.copy();
		newE.setType(type);
		return newE;
	}
	
	/**
	 * Copies this Expr, sets the irNode of the copied Expr,
	 * and returns that copied Expr.
	 * @param node the IRNode to set the copied Expr
	 * @return the copied Expr
	 */
	public <E extends Expr> E copyAndSetIRExpr(IRExpr expr) {
		E newE = this.copy();
		newE.setIrExpr(expr);
		return newE;
	}
}
