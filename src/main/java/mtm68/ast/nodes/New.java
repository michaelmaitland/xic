package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class New extends Expr {
	
	private String className;
	private FExpr fExpr;

	public New(String className, FExpr fExpr) {
		this.className = className;
		this.fExpr = fExpr;
	}
	
	@Override
	public String toString() {
		return "new " + className + fExpr.toString() + "";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("new " + className);
		fExpr.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		FExpr newFExpr = fExpr.accept(v);
		if (newFExpr != fExpr) {
			New newNew = copy();
			newNew.fExpr = newFExpr;
			return newNew;
        } else {
            return this; 
        }	
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		// TODO
		return null;
	}
}
