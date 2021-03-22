package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Var extends Expr {
	
	private String id;
	
	public Var(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom(id);
	}

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkVar(this);
		return copyAndSetType(type);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
