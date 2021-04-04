package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.Block;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class FunctionDefn extends Node {
	
	private FunctionDecl functionDecl;
	private Block body;
	
	private  IRFuncDefn irFuncDefn;

	public FunctionDefn(FunctionDecl fDecl, Block body) {
		this.functionDecl = fDecl;
		this.body = body;
	}

	public FunctionDecl getFunctionDecl() {
		return functionDecl;
	}
	
	public Block getBody() {
		return body;
	}

	public IRFuncDefn getIrFuncDefn() {
		return irFuncDefn;
	}

	public void setIrFuncDefn(IRFuncDefn irFuncDefn) {
		this.irFuncDefn= irFuncDefn;
	}

	@Override
	public String toString() {
		return "FunctionDefn [fDecl=" + functionDecl + ", body=" + body + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		functionDecl.prettyPrint(p);
		body.prettyPrint(p);
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		FunctionDecl newFunctionDecl = functionDecl.accept(v);
		Block newBody = body.accept(v);

		if(newFunctionDecl!= functionDecl || newBody != body) {
			FunctionDefn defn = copy();
			defn.functionDecl = newFunctionDecl;
			defn.body = newBody;
			return defn;
		}
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkFunctionResult(this);
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory irFactory) {

		String funcName = cv.getFuncSymbol(functionDecl);
		IRSeq seq = irFactory.IRSeq(body.getIRStmt());
		IRFuncDefn defn = irFactory.IRFuncDefn(funcName, seq);
				
		FunctionDefn copy = copy();
		copy.setIrFuncDefn(defn);
		return copy;
	}
}
