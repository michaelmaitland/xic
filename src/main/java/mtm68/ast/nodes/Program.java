package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class Program extends Node implements Root {
	
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

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		
		// Use Statements
		p.startList();
		for(Use use : useStmts) use.prettyPrint(p);
		p.endList();
		
		// Func Decls
		p.startUnifiedList();
		for(FunctionDefn defn : fDefns) defn.prettyPrint(p);
		p.endList();
		
		p.printAtom("\n");
		p.endList();
	}
	
	
}
