package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRExpr;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Type;

public abstract class Expr extends Node implements IExpr, HasType {

	protected Type type;
	
	protected IRExpr irExpr;
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public IRExpr getIRExpr() {
		return irExpr;
	}

	@Override
	public void setIRExpr(IRExpr irExpr) {
		this.irExpr = irExpr;
	}

	public <E extends Expr> E copyAndSetType(Type type) {
		E newE = this.copy();
		newE.setType(type);
		return newE;
	}
	
	public <E extends Expr> E copyAndSetIRExpr(IRExpr expr) {
		E newE = this.copy();
		newE.setIRExpr(expr);
		return newE;
	}
}
