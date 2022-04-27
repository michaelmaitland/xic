package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class MethodCall extends Expr {
	
	private Var obj;
	private FExpr fExpr;

	public MethodCall(Var obj, FExpr fExpr) {
		this.obj = obj;
		this.fExpr = fExpr;
	}
	
	public MethodCall(FExpr fExpr) {
		this(new Var("this"), fExpr);
	}

	public Var getObj() {
		return obj;
	}
	
	public FExpr getFExpr() {
		return fExpr;
	}
	
	@Override
	public String toString() {
		return "MethodCall[obj=" + obj + ", fexpr=" + fExpr+ "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		obj.prettyPrint(p);
		fExpr.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Var newObj = obj.accept(v);
		FExpr newFExpr = fExpr.accept(v);
		if(newObj != obj || newFExpr!= fExpr) {
			MethodCall newMethodCall = copy();
			newMethodCall.obj = newObj;
			newMethodCall.fExpr = newFExpr;
			return newMethodCall;
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		// TODO
		return this;
	}
}
