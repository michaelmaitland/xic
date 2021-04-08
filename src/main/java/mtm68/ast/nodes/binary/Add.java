package mtm68.ast.nodes.binary;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;

public class Add extends BinExpr {

	public Add(Expr left, Expr right) {
		super(Binop.ADD, left, right);
		setType(Types.INT);
	}
	
	
	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {

		if(isArrayAddition(left, right)) {
			IRExpr leftArr = left.getIRExpr();
			IRExpr rightArr = right.getIRExpr();
			IRESeq eseq = cv.concatArrays(leftArr, rightArr);
			return copyAndSetIRExpr(eseq);

		} else if (isIntAddition(left, right)){

			IRBinOp node = inf.IRBinOp(op.convertToOpType(), left.getIRExpr(), right.getIRExpr());
			return copyAndSetIRExpr(node);

		} else {
			throw new InternalCompilerError("Failed to support addition between types " + left.getType() + " and " + right.getType());
		}
	}
	
	private boolean isArrayAddition(Expr left, Expr right) {
		return Types.isArray(left.getType()) && Types.isArray(right.getType());
	}
	
	private boolean isIntAddition(Expr left, Expr right) {
		return left.getType() == Types.INT 
				&& right.getType() == Types.INT;
	}
}
