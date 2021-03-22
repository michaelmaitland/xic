package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Types;
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
	
	public Expr getLeft() {
		return left;
	}

	public void setLeft(Expr left) {
		this.left = left;
	}

	public Expr getRight() {
		return right;
	}

	public void setRight(Expr right) {
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
		Expr newLeft = left.accept(v);
		Expr newRight = right.accept(v);
		
		if(newLeft != left || newRight != right) {
			return new BinExpr(op, left, right);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkBinExpr(left, right);
		
		BinExpr copy = copy();
		copy.setType(Types.INT);
		return copy;
	}
}
