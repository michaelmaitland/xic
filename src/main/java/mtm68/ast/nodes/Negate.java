package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Negate extends UnExpr {

	public Negate(Expr expr) {
		super(expr);
	}
	
	@Override
	public String toString() {
		return "(- " + expr.toString() + ")";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("-");
		expr.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newExpr = expr.accept(v);
		if (newExpr != expr) {
			Negate newNegate = copy();
			newNegate.expr = newExpr;
			return newNegate;
        } else {
            return this; // no new node needed
        }	
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkNegate(this);
		return copyAndSetType(Types.INT);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		// NEG(e) = SUB(0,e)
		IRBinOp op = new IRBinOp(OpType.SUB, new IRConst(0), expr.getIRExpr());
		return copyAndSetIRExpr(op);
	}
}
