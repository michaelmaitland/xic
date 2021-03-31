package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;

public class And extends BinExpr {

	public And(Expr left, Expr right) {
		super(Binop.AND, left, right);
		setType(Types.BOOL);
	}
	
	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		IRBinOp node = new IRBinOp(op.convertToOpType(), left.getIrExpr(), right.getIrExpr());
		return copyAndSetIRExpr(node);
	}
}
