package mtm68.ast.nodes.stmts;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.ArrayIndex;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.types.HasType;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.visit.FunctionInliner;
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
		// Check RHS before LHS so we cant use vars declared on LHS on RHS
		Expr newRhs = rhs.accept(v);
		Node newLhs = lhs.accept(v);
		
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
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		if(lhs instanceof Var) {
			Var var = (Var)lhs;
			IRMove move = inf.IRMove(var.getIRExpr(), rhs.getIRExpr());
			return copyAndSetIRStmt(move);
		} else if (lhs instanceof ArrayIndex) {
			IRSeq seq = convertArrayIndexAssign(cv, inf, (ArrayIndex)lhs);
			return copyAndSetIRStmt(seq);
		} else if (lhs instanceof SimpleDecl) {
			SimpleDecl decl = (SimpleDecl)lhs;
			IRMove move = inf.IRMove(inf.IRTemp(cv.newTemp(decl.getId())), rhs.getIRExpr());
			return copyAndSetIRStmt(move);
		} else {
			throw new InternalCompilerError("Failed to convert to IR for Single Assign");
		}
	}
	
	public IRSeq convertArrayIndexAssign(NodeToIRNodeConverter cv, IRNodeFactory inf, ArrayIndex ai) {
		IRTemp tempArr = inf.IRTemp(cv.newTemp());
		IRTemp tempIndex = inf.IRTemp(cv.newTemp());
		
		IRMem offsetIntoArr = cv.getOffsetIntoArr(tempArr, tempIndex);

		return inf.IRSeq(
			inf.IRMove(tempArr, ai.getArr().getIRExpr()),
			inf.IRMove(tempIndex, ai.getIndex().getIRExpr()),
			cv.boundsCheck(tempArr, tempIndex),
		    inf.IRMove(offsetIntoArr, rhs.getIRExpr())
		);
	}
	
	@Override
	public Node renameVars(Map<String, String> varMap) {
		SingleAssign sa = this.copy();
		
		sa.lhs = lhs.renameVars(varMap);
		sa.rhs = (Expr) rhs.renameVars(varMap);
		
		return sa;
	}
	
	@Override
	public Node functionInline(FunctionInliner fl) {
		return fl.transformSingleAssign(this);
	}
}