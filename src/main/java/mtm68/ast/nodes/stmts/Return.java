package mtm68.ast.nodes.stmts;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Return extends Statement {
	
	private List<Expr> retList;

	public Return(List<Expr> retList) {
		this.retList = retList;
	}

	@Override
	public String toString() {
		return "Return [retList=" + retList + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("return");
		for(Expr expr: retList) expr.prettyPrint(p);
		p.endList();
	}
	
	public List<Expr> getRetList() {
		return retList;
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<Expr> retList = visitList(this.retList, v);
		
		// TODO check copy
		return new Return(retList);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
