package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.Optional;

import mtm68.ast.nodes.FExpr;

public class MultipleAssign extends Assign {
	
	private List<Optional<ExtendedDecl>> decls;
	private FExpr rhs;
	
	public MultipleAssign(List<Optional<ExtendedDecl>> decls, FExpr rhs) {
		this.decls = decls;
		this.rhs = rhs;
	}

	@Override
	public String toString() {
		return "MultipleAssign [decls=" + decls + ", rhs=" + rhs + "]";
	}
}
