package mtm68.ast.nodes;

import mtm68.ast.nodes.stmts.Block;

public class FunctionDefn extends Node {
	
	private FunctionDecl fDecl;
	private Block body;

	public FunctionDefn(FunctionDecl fDecl, Block body) {
		this.fDecl = fDecl;
		this.body = body;
	}

	@Override
	public String toString() {
		return "FunctionDefn [fDecl=" + fDecl + ", body=" + body + "]";
	}

}
