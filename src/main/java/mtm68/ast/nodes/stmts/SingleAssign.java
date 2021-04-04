package mtm68.ast.nodes.stmts;

import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.exception.InternalCompilerException;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class SingleAssign extends Assign {
	
	// TODO change parser to return a node
	private Node lhs;
	private Expr rhs;

	public SingleAssign(Node lhs, Expr rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Node getLhs() {
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
		Node newLhs = lhs.accept(v);
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
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory irFactory) {
		/**
		 * The lhs can be a Var, an ArrayIndex, or a SimpleDecl.
		 * However, a SimpleDecl gets converted to a Var so
		 * we can do an IRMove on it, since its IR representation
		 * is an IRExpr instead of an IRStmt.
		 */
		if(lhs instanceof Var) {
			Var var = (Var)lhs;
			IRMove move = irFactory.IRMove(var.getIRExpr(), rhs.getIRExpr());
			return copyAndSetIRStmt(move);
		} else if (lhs instanceof ArrayIndex) {
			IRSeq seq = convertArrayIndexAssign(cv, irFactory, (ArrayIndex)lhs);
			return copyAndSetIRStmt(seq);
		} else {
			throw new InternalCompilerException();
		}
	}
	
	public IRSeq convertArrayIndexAssign(NodeToIRNodeConverter cv, IRNodeFactory irFactory, ArrayIndex ai) {
		IRTemp tempArr = irFactory.IRTemp(cv.newTemp());
		IRTemp tempIndex = irFactory.IRTemp(cv.newTemp());
		
		IRMem offsetIntoArr = cv.getOffsetIntoArr(tempArr, tempIndex);

		return irFactory.IRSeq(
			irFactory.IRMove(tempArr, ai.getArr().getIRExpr()),
			irFactory.IRMove(tempIndex, ai.getIndex().getIRExpr()),
			cv.boundsCheck(tempArr, tempIndex),
		    irFactory.IRMove(offsetIntoArr, rhs.getIRExpr())
		);
	}
}