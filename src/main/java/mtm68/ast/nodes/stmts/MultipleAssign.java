package mtm68.ast.nodes.stmts;

import java.util.ArrayList;
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
		if(decls == null) {
			return null;
		}
			
		List<Optional<SimpleDecl>> newDecls = decls;
		List<Optional<SimpleDecl>> vl = new ArrayList<>(decls.size());
		
		for(Optional<SimpleDecl> d : decls) {
			if(!d.isPresent()) {
				vl.add(d);
				continue;
			} else {
				SimpleDecl s = d.get();
				SimpleDecl newS = s.accept(v); 
				if(newS != s) {
					newDecls = vl;
				}
				vl.add(Optional.of(newS));
			}
		}
		
		FExpr newRhs = rhs.accept(v);
		
		
		if(newRhs != rhs || newDecls != decls) {
			return new MultipleAssign(newDecls, newRhs);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
