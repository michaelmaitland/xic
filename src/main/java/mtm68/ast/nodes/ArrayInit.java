package mtm68.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class ArrayInit extends Expr {
	
	private List<Expr> items;

	public ArrayInit(List<Expr> items) {
		this.items = items;
	}

	public List<Expr> getItems() {
		return items;
	}

	public void setItems(List<Expr> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "ArrayInit [items=" + items + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();	
		for(Expr item : items) item.prettyPrint(p);	
		p.endList(); 
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<Expr> newItems = acceptList(items, v);
		if(newItems != items) {
			ArrayInit newArrayInit = copy();
			newArrayInit.items = newItems;
			return newArrayInit;
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkArrayInit(this);
		return copyAndSetType(type);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		
		IRTemp arrBase = new IRTemp(cv.newTemp());
		IRConst sizeOfArrAndLen = new IRConst(items.size() * cv.getWordSize() + cv.getWordSize());
		IRName malloc = new IRName(cv.getMallocLabel());

		List<IRStmt> seq = new ArrayList<>();
		// alloc array and move addr into temp
		seq.add(new IRMove(arrBase, new IRCall(malloc, sizeOfArrAndLen)));
		// store length of array
		seq.add(new IRMove(new IRMem(arrBase), new IRConst(items.size())));

		// put items in their index
		for(int i=0; i < items.size(); i++) {
			IRBinOp offset = new IRBinOp(OpType.MUL, new IRConst(items.size()), new IRConst(cv.getWordSize()));
			IRBinOp elem = new IRBinOp(OpType.ADD, arrBase, offset); 
			seq.add(new IRMove(new IRMem(elem), items.get(i).getIrExpr()));
		}
		
		IRBinOp startOfArr = new IRBinOp(OpType.ADD, arrBase, new IRConst(cv.getWordSize()));
		IRESeq eseq =  new IRESeq(new IRSeq(seq), startOfArr);
		
		return copyAndSetIRExpr(eseq);
	}
}
