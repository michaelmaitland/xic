package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.SingleAssignLHS;

public class ArrayIndex extends Expr implements SingleAssignLHS {
	
	private Expr arr;
	private Expr index;

	public ArrayIndex(Expr arr, Expr index) {
		this.arr = arr;
		this.index = index;
	}
	
	@Override
	public String toString() {
		return "(" + arr.toString() + "[" + index.toString() + "])";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("[");
		p.printAtom("]");
		arr.prettyPrint(p);
		index.prettyPrint(p);
		p.endList();
	}
	
	public Expr getArr() {
		return arr;
	}
	
	public Expr getIndex() {
		return index;
	}

}
