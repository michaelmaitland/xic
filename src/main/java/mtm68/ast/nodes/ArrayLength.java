package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ArrayLength extends Expr {
	
	private Expr exp;

	public ArrayLength(Expr exp) {
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
		Expr exp = visitChild(this.exp, v);
		
		// TODO check if we need to copy
		return new ArrayLength(exp);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}