package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
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
			return new ArrayLength(newExp);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkArrayLength(this);
		return copyAndSetType(type);
	}
	
	
}