package mtm68.ast.nodes.stmts;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;

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
}
