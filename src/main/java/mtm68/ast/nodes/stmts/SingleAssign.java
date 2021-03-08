package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;

public class SingleAssign extends Assign {
	
	private SingleAssignLHS lhs;
	private Expr rhs;

	public SingleAssign(SingleAssignLHS lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	@Override
	public String toString() {
		return "SingleAssign [lhs=" + lhs + ", rhs=" + rhs + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("=");
		p.startList();
		String lhsString = lhs.getType().isPresent() ? lhs.getName() + " " + lhs.getType().get() : lhs.getName();
		p.printAtom(lhsString);
		p.endList();
		
		rhs.prettyPrint(p);
		
		p.endList();
	}
	
	
	
	
}