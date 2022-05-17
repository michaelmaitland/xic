package mtm68.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.ObjectType;
import mtm68.util.FreshTempGenerator;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.ThisAugmenter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class MethodCall extends Expr {
	
	private Var obj;
	private FExpr fExpr;

	public MethodCall(Var obj, FExpr fExpr) {
		this.obj = obj;
		this.fExpr = fExpr;
		fExpr.setIsMethodCall(true);
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
	public Node augmentWithThis(ThisAugmenter ta) {
		// change o.f() to o.f(o)
		MethodCall newMethodCall = copy();
		newMethodCall.getFExpr().getArgs().add(0, obj);
		return newMethodCall;
	}
	
	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		
		IRMem dv = cv.getOffsetIntoArr(obj.getIRExpr(), inf.IRConst(0));

		// It doesn't matter whether we have the concrete class
		// name here because all types that declare this
		// method put it at same index.
		ObjectType type = (ObjectType)obj.getType();
		// TODO typecheck and use type
		IRMem name = cv.getMethodSymbol(dv, fExpr.getId(), "A");
		
		// Note that the object is already an arg of the FExpr because of augmentWithThis
		List<IRExpr> irArgs = fExpr.getArgs()
				.stream()
				.map(Expr::getIRExpr).collect(Collectors.toList());

		IRCallStmt call = inf.IRCallStmt(name, irArgs);
		IRTemp freshTemp = inf.IRTemp(FreshTempGenerator.getFreshTemp());
		IRMove moveIntoFresh = inf.IRMove(freshTemp, inf.IRTemp(cv.retVal(0)));
		IRESeq eseq = inf.IRESeq(inf.IRSeq(call, moveIntoFresh), freshTemp);

		return copyAndSetIRExpr(eseq);
	}
}
