package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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
		lhs.prettyPrint(p);		
		rhs.prettyPrint(p);
		
		p.endList();
	}
	
	public SingleAssignLHS getLhs() {
		return lhs;
	}
	
	public Expr getRhs() {
		return rhs;
	}

	@Override
	public Node visitChildren(Visitor v) {
		SingleAssignLHS lhs = visitChild(lhs, v);
		Expr rhs = visitChild(rhs, v);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}