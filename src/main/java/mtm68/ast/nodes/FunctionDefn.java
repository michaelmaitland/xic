package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
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

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		fDecl.prettyPrint(p);
		body.prettyPrint(p);
		p.endList();
	}

}
