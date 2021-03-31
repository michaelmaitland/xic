package mtm68.ast.nodes.binary;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class BinExpr extends Expr {
	
	protected Binop op;
	protected Expr left;
	protected Expr right;

	public BinExpr(Binop op, Expr left, Expr right) {
		this.op = op;
		this.left = left;
		this.right = right;
	}
	
	public Binop getOp() {
		return op;
	}

	public void setOp(Binop op) {
		this.op = op;
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
			BinExpr newBinExpr = copy();
			newBinExpr.op = op;
			newBinExpr.left = newLeft;
			newBinExpr.right = newRight;
			return newBinExpr;
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkBinExpr(this);
		return copyAndSetType(type);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		IRBinOp node = new IRBinOp(op.convertToOpType(), left.getIrExpr(), right.getIrExpr());
		return copyAndSetIRExpr(node);
	}
}
