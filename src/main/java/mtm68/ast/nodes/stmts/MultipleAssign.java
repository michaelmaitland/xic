package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
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

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("=");
		p.startList();
		for(Optional<ExtendedDecl> optDecl : decls)
			if(optDecl.isPresent()) optDecl.get().prettyPrint(p);
			else p. printAtomNoSpace("_ ");
		p.endList();
		rhs.prettyPrint(p);
		p.endList();
	}
}
