package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.visit.NodeToIRNodeConverter;

public class Or extends BinExpr {

	public Or(Expr left, Expr right) {
		super(Binop.OR, left, right);
	}
	
	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		IRESeq eseq = cv.convertNonCtrlFlowBoolean(this);
		return copyAndSetIRExpr(eseq);
	}
}
