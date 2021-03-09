package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class Program extends Node implements Root {
	
	private List<Use> useStmts;
	private List<FunctionDefn> functionDefns;

	public Program(List<Use> useStmts, List<FunctionDefn> fDefns) {
		this.useStmts = useStmts;
		this.functionDefns = fDefns;
	}

	@Override
	public String toString() {
		return "Program [useStmts=" + useStmts + ", fDefns=" + functionDefns + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();

		// Use Statements
		p.startUnifiedList();
		for(Use use : useStmts) use.prettyPrint(p);
		p.endList();
		
		// Func Decls
		
		p.startUnifiedList();
		for(FunctionDefn defn : functionDefns) defn.prettyPrint(p);
		p.printAtom("");
		p.endList();
		
		p.printAtom("");
		p.endList();
	}
	
	public List<Use> getUseStmts() {
		return useStmts;
	}
	
	public List<FunctionDefn> getFunctionDefns() {
		return functionDefns;
	}
}
