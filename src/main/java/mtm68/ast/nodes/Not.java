package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Not extends UnExpr {

	public Not(Expr expr) {
		super(expr);
	}
	
	@Override
	public String toString() {
		return "(! " + expr.toString() + ")";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("!");
		expr.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newExpr = expr.accept(v);
		if (newExpr != expr) {
			Not newNot = copy();
			newNot.expr = newExpr;
			return newNot;
        } else {
            return this; 
        }	
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkNot(this);
		return copyAndSetType(Types.BOOL);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory irFactory) {
		// NOT(e) = XOR(1, e)
		IRExpr left = irFactory.IRConst(1);
		IRBinOp op = irFactory.IRBinOp(OpType.XOR, left, expr.getIRExpr());
		return copyAndSetIRExpr(op);
	}
}
