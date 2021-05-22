package mtm68.ast.nodes.stmts;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Return extends Statement {
	
	private List<Expr> retList;

	public Return(List<Expr> retList) {
		this.retList = retList;
	}

	public List<Expr> getRetList() {
		return retList;
	}

	@Override
	public String toString() {
		return "Return [retList=" + retList + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("return");
		for(Expr expr: retList) expr.prettyPrint(p);
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<Expr> newRetList = acceptList(retList, v);
		
		if(newRetList != retList) {
			Return ret = copy(); 
			ret.retList = newRetList;

			return ret;
		} 
		
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkReturn(this, retList);

		Return ret = copy();
		ret.result = Result.VOID;

		return ret;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		List<IRExpr> rets = retList.stream()
								   .map(Expr::getIRExpr)
								   .collect(Collectors.toList());
		
		return copyAndSetIRStmt(inf.IRReturn(rets));
	}
}
