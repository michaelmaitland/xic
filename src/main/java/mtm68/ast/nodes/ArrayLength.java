package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ArrayLength extends Expr {
	
	private Expr exp;

	public ArrayLength(Expr exp) {
		this.exp = exp;
	}

	public Expr getExp() {
		return exp;
	}

	public void setExp(Expr exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "length [exp=" + exp + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("length");
		exp.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newExp = this.exp.accept(v);
		if(newExp != exp) {
			ArrayLength newArrayLength = copy();
			newArrayLength.exp = newExp;
			return  newArrayLength;
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkArrayLength(this);
		return copyAndSetType(Types.INT);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		IRMem lengthSlot = cv.getOffsetIntoArr(exp.getIRExpr(), inf.IRConst(-1), true);
		return copyAndSetIRExpr(lengthSlot);
	}
}