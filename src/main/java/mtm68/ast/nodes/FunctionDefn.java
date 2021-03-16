package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.stmts.Block;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class FunctionDefn extends Node {
	
	private FunctionDecl functionDecl;
	private Block body;

	public FunctionDefn(FunctionDecl fDecl, Block body) {
		this.functionDecl = fDecl;
		this.body = body;
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
	
	public FunctionDecl getFunctionDecl() {
		return functionDecl;
	}
	
	public Block getBody() {
		return body;
	}

	@Override
	public Node visitChildren(Visitor v) {
		FunctionDecl functionDecl = visitChild(this.functionDecl, v);
		Block body = visitChild(this.body, v);
		
		// TODO: check if need copy
		return new FunctionDefn(functionDecl, body);
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}

}
