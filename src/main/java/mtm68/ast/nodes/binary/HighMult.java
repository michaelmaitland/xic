package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;

public class HighMult extends BinExpr {

	public HighMult(Expr left, Expr right) {
		super(Binop.HIGH_MULT, left, right);
	}
}
