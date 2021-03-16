package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.Optional;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class MultipleAssign extends Assign {
	
	private List<Optional<SimpleDecl>> decls;
	private FExpr rhs;
	
	public MultipleAssign(List<Optional<SimpleDecl>> decls, FExpr rhs) {
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
		for(Optional<SimpleDecl> optDecl : decls)
			if(optDecl.isPresent()) optDecl.get().prettyPrint(p);
			else p. printAtom("_ ");
		p.endList();
		rhs.prettyPrint(p);
		p.endList();
	}
	
	public List<Optional<SimpleDecl>> getDecls() {
		return decls;
	}
	
	public FExpr getRhs() {
		return rhs;
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<Optional<SimpleDecl>> decls = visitChild(this.decls, v);
		FExpr rhs = visitChild(rhs, v);
		
		// TODO check copy
		return new MultipleAssign(decls, rhs);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
