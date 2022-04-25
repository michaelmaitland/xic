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
import mtm68.ast.types.Type;
import mtm68.util.ArrayUtils;
import mtm68.util.FreshTempGenerator;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class New extends Expr {
	
	private String className;
	private FExpr fExpr;

	public New(String className, FExpr fExpr) {
		this.className = className;
		this.fExpr = fExpr;
	}
	
	@Override
	public String toString() {
		return "new " + className + fExpr.toString() + "";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("new " + className);
		fExpr.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		FExpr newFExpr = fExpr.accept(v);
		if (newFExpr != fExpr) {
			New newNew = copy();
			newNew.fExpr = newFExpr;
			return newNew;
        } else {
            return this; 
        }	
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkNew(this);
		return copyAndSetType(type);
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		List<IRExpr> exprs = ArrayUtils.empty();

		IRMem dispatchVectorPointer = cv.getDispatchVectorAddr(className);
		exprs.add(dispatchVectorPointer);
		
		int numFields = cv.getNumFields(className);
		for(int i = 0; i < numFields; i++) {
			exprs.add(inf.IRConst(0)); // Default all fields to 0
		}

		// allocate the object that contains dv ptr + fields
		IRESeq object = cv.allocateAndInitArray(exprs);
		
		// FExpr must have the object as first argument
		// We rebuild the FExpr IR here and use the new version instead 
		String sym = cv.getFuncSymbol(fExpr);
		IRName name = inf.IRName(sym);
		List<IRExpr> irArgs = fExpr.getArgs()
							       .stream()
								   .map(Expr::getIRExpr)
								   .collect(Collectors.toList());
		// Prepend the object argument
		irArgs.add(0, object);

		IRCallStmt call = inf.IRCallStmt(name, irArgs);
		IRTemp freshTemp = inf.IRTemp(FreshTempGenerator.getFreshTemp());
		IRMove moveIntoFresh = inf.IRMove(freshTemp, inf.IRTemp(cv.retVal(0)));
		IRESeq eseq = inf.IRESeq(inf.IRSeq(call, moveIntoFresh), freshTemp);

		return copyAndSetIRExpr(eseq);
	}
}
