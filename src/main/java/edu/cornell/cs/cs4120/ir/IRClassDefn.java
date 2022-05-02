package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsExprWithSideEffect;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.util.ArrayUtils;

/** An IR class definition */
public class IRClassDefn extends IRNode_c {
    private String className;
    private List<IRFuncDefn> methods;
    private IRDataArray dispatchVector;

    public IRClassDefn() {
    	this.methods = ArrayUtils.empty();
    }
    
    public IRClassDefn(String className, List<IRFuncDefn> methods, IRDataArray dispatchVector) {
    	this.className = className;
    	this.methods = methods;
    	this.dispatchVector = dispatchVector;
    }

	public String getClassName() {
		return className;
	}

	public List<IRFuncDefn> getMethods() {
		return methods;
	}

	public IRDataArray getDispatchVector() {
		return dispatchVector;
	}

	@Override
	public IRNode lower(Lowerer v) {
		return this;
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		/*
		 * Avaliable Expressions is computed interproceduraly. If you want to do AE
		 * analysis, you should get all methods and call their genAvaliableExprs()
		 * method.
		 */
		throw new RuntimeException("Avaliable Expressions is computed interproceduraly. "
				+ "If you want to do AE analysis, you should get all methods and "
				+ "call their genAvaliableExprs() method.");
	}

	@Override
	public Set<IRTemp> use() {
		/*
		 * use() analysis is computed interproceduraly. If you want to do use analysis,
		 * you should get all methods and call their use() method.
		 */
		throw new RuntimeException("use() analysis is computed interproceduraly. "
				+ "If you want to do use analysis, you should get all methods and call their use() method.");
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		/*
		 * containsExpr() analysis is computed interproceduraly. If you want to do
		 * containsExpr analysis, you should get all methods and call their
		 * containsExpr() method.
		 */
		throw new RuntimeException("containsExpr() analysis is computed interproceduraly. "
				+ "If you want to do containsExpr analysis, you should get all methods "
				+ "and call their containsExpr() method.");
	}

	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		/*
		 * replaceExpr() transformation is computed interproceduraly. If you want to do
		 * replaceExpr transformation, you should get all methods and call their
		 * containsExpr() method.
		 */
		throw new RuntimeException("replaceExpr() transformation is computed interproceduraly. "
				+ "If you want to do replaceExpr transformation, you should get all "
				+ "methods and call their containsExpr() method.");
	}

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRNode decorateContainsExprWithSideEffect(IRContainsExprWithSideEffect irContainsExprWithSideEffect) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String label() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printSExp(SExpPrinter p) {
		// TODO Auto-generated method stub
		
	}
}
