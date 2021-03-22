package mtm68.ast.nodes.binary;

import mtm68.ast.nodes.Expr;
import mtm68.ast.types.Types;

public class Add extends BinExpr {

	public Add(Expr left, Expr right) {
		super(Binop.ADD, left, right);
		setType(Types.INT);
	}
}
