package mtm68.ast.nodes;

import java.util.List;

public class Program extends Node {
	
	private List<Use> useStmts;
	private List<FunctionDefn> fDefns;

	public Program(List<Use> useStmts, List<FunctionDefn> fDefns) {
		this.useStmts = useStmts;
		this.fDefns = fDefns;
	}

	@Override
	public String toString() {
		return "Program [useStmts=" + useStmts + ", fDefns=" + fDefns + "]";
	}
}
