package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ArrayIndex extends Expr  {
	
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
		p.printAtom("[]");
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

	@Override
	public Node visitChildren(Visitor v) {
		Expr newArr = arr.accept(v);
        Expr newIndex = index.accept(v);
        if (newArr != arr|| newIndex != index) {
            return new ArrayIndex(newArr, newIndex);
        } else {
            return this; // no new node needed
        }
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkArrayIndex(this);
		
		ArrayIndex newArrayIndex = copy();
		newArrayIndex.setType(type);
		return newArrayIndex;
	}
}
