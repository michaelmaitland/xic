package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class BinExpr extends Expr {
	
	private Binop op;
	private Expr left;
	private Expr right;

	public BinExpr(Binop op, Expr left, Expr right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String toString() {
		return "(" + left + " " + op + " " + right + ")";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(op.toString());
		left.prettyPrint(p);
		right.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr left = visitChild(this.left, v);
		Expr right = visitChild(this.right, v);
		
		// TODO: check copy
		return new BinExpr(op, left, right);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
