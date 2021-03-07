package mtm68.ast.nodes.stmts;

import mtm68.ast.nodes.Expr;

public class SingleAssign extends Assign {
	
	private SingleAssignLHS lhs;
	private Expr rhs;

	public SingleAssign(SingleAssignLHS lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public String toString() {
		return "SingleAssign [lhs=" + lhs + ", rhs=" + rhs + "]";
	}
	
	
}