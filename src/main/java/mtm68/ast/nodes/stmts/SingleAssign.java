package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.LHS;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class SingleAssign extends Assign {
	
	// TODO change parser to return a node
	private LHS lhs;
	private Expr rhs;

	public SingleAssign(LHS lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public LHS getLhs() {
		return lhs;
	}
	
	public Expr getRhs() {
		return rhs;
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
	
	@Override
	public Node visitChildren(Visitor v) {
		LHS newLhs = lhs.accept(v);
		Expr newRhs = rhs.accept(v);
		
		if(newLhs != lhs || newRhs != rhs) {
			SingleAssign single = copy();
			single.lhs = newLhs;
			single.rhs = newRhs;

			return single;
		} 
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// This is a safe-cast, but we should be careful
		Type lhsType = ((HasType) lhs).getType();
		tc.checkType(rhs, lhsType);
		
		SingleAssign single = copy();
		single.result = Result.UNIT;

		return single;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		IRMove move = new IRMove(lhs.getIrExpr(), rhs.getIrExpr());
		return copyAndSetIRStmt(move);
	}
}