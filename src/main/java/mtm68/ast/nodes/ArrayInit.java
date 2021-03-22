package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
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
			return new ArrayInit(newItems);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkArrayInit(this);
		return copyAndSetType(type);
	}
	
	
}
