package mtm68.ast.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.types.Result;
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

	public IRFuncDefn getIRFuncDefn() {
		return irFuncDefn;
	}

	public void setIRFuncDefn(IRFuncDefn irFuncDefn) {
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
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {

		String funcName = cv.saveAndGetFuncSymbol(functionDecl);
		
		IRSeq seq = cv.constructFuncDefnSeq(functionDecl, body);
		IRFuncDefn defn = inf.IRFuncDefn(funcName, seq);
				
		FunctionDefn copy = copy();
		copy.setIRFuncDefn(defn);
		return copy;
	}
}
