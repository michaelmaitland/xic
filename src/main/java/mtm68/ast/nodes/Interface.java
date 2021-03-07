package mtm68.ast.nodes;

import java.util.List;

public class Interface extends Node {
	
	private List<FunctionDecl> fDecls;

	public Interface(List<FunctionDecl> fDecls) {
		this.fDecls = fDecls;
	}

	@Override
	public String toString() {
		return "Interface [fDecls=" + fDecls + "]";
	}
}
