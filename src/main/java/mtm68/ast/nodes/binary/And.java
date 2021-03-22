package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;
import mtm68.ast.types.Types;

public class And extends BinExpr {

	public And(Expr left, Expr right) {
		super(Binop.AND, left, right);
		setType(Types.BOOL);
	}
}
