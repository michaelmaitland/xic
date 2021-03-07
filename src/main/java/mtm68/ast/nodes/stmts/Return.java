package mtm68.ast.nodes.stmts;

import java.util.List;

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
}
