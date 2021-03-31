package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ArrayIndex extends Expr implements LHS  {
	
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
	public Node convertToIR(NodeToIRNodeConverter cv) {
	
		IRTemp tempArr = new IRTemp(cv.newTemp());
		IRTemp tempIndex = new IRTemp(cv.newTemp());

		// Add Bounds checking
		IRLabel ok = new IRLabel(cv.getFreshLabel());
		String errLabel = cv.getOutOfBoundsLabel();
		IRMem lenAddr = new IRMem(new IRBinOp(OpType.SUB, tempArr, new IRConst(cv.getWordSize())));
		IRBinOp boundsCheck = new IRBinOp(OpType.ULT, tempIndex, lenAddr);

		IRSeq seq = new IRSeq(new IRMove(tempArr, arr.getIrExpr()),
							  new IRMove(tempIndex, index.getIrExpr()),
							  new IRCJump(boundsCheck, ok.name(), errLabel),
							  ok);

		/*
		 * index is going to be at mem address: (mem addr of arr) + (WORD_SIZE * index).
		 * We can us the temp's here because it will be executed after
		 * a seq that does the temp setup
		 */
		IRExpr e = new IRBinOp(OpType.MUL, new IRConst(cv.getWordSize()), tempIndex);
		IRExpr e2 = new IRBinOp(OpType.ADD, tempArr, e); 
		IRMem mem = new IRMem(e2);
	
		IRESeq eseq = new IRESeq(seq, mem);

		return copyAndSetIRExpr(eseq);
	}
}
