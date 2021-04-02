package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRExpr;
import mtm68.ast.types.Type;

public interface IExpr extends INode {
	
	public Type getType();

	public void setType(Type type);

	public IRExpr getIRExpr();
	
	public void setIRExpr(IRExpr irExpr);
	
	/**
	 * Copies this Expr, sets the type of the copied Expr,
	 *  and returns that copied Expr.
	 * @param type the type to set the copied Expr
	 * @return the copied Expr
	 */
	public <E extends Expr> E copyAndSetType(Type type);

	/**
	 * Copies this Expr, sets the irNode of the copied Expr,
	 * and returns that copied Expr.
	 * @param node the IRNode to set the copied Expr
	 * @return the copied Expr
	 */	
	public <E extends Expr> E copyAndSetIRExpr(IRExpr expr);
}
