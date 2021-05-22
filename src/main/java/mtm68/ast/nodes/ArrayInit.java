package mtm68.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
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
    public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		
		List<IRExpr> elems = items.stream()
								  .map(Expr::getIRExpr)
								  .collect(Collectors.toList());

		IRESeq eseq = cv.allocateAndInitArray(elems);

        return copyAndSetIRExpr(eseq);
    }
}
