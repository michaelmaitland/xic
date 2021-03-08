package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class ArrayIndex extends Expr {
	
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
	
	

}
