package mtm68.ast.nodes;

import java.util.List;

import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.types.Type;

public class FunctionDecl extends Node {
	
	private String id;
	private List<SimpleDecl> args;
	private List<Type> returnTypes;

	public FunctionDecl(String id, List<SimpleDecl> args, List<Type> returnTypes) {
		this.id = id;
		this.args = args;
		this.returnTypes = returnTypes;
	}

	@Override
	public String toString() {
		return "FunctionDecl [id=" + id + ", args=" + args + ", returnTypes=" + returnTypes + "]";
	}
}
