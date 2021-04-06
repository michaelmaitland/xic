package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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
        	ArrayIndex newArrayIndex = copy();
        	newArrayIndex.arr = newArr;
        	newArrayIndex.index = newIndex;
        	return newArrayIndex;
        } else {
            return this;
        }
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkArrayIndex(this);
		return copyAndSetType(type);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		
		IRTemp tempArr = inf.IRTemp(cv.newTemp());
		IRTemp tempIndex = inf.IRTemp(cv.newTemp());
		
		IRSeq seq = inf.IRSeq(
				inf.IRMove(tempArr, arr.getIRExpr()),
				inf.IRMove(tempIndex, index.getIRExpr()),
				cv.boundsCheck(tempArr, tempIndex));
		
		IRMem offsetIntoArr = cv.getOffsetIntoArr(tempArr, tempIndex);
		IRESeq eseq = inf.IRESeq(seq, offsetIntoArr);

		return copyAndSetIRExpr(eseq);
	}
}
