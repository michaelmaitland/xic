package mtm68.ast.nodes;

import mtm68.ast.types.HasType;
import mtm68.ast.types.Type;

public abstract class Expr extends Node implements HasType {

	protected Type type;
	
	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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
}
