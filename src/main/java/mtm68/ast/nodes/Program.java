package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

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
		p.endList();
		
		p.endList();
	}
	
	public List<Use> getUseStmts() {
		return useStmts;
	}
	
	public List<FunctionDefn> getFunctionDefns() {
		return functionDefns;
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<Use> newUseStmts = acceptList(useStmts, v);
		List<FunctionDefn> newFunctionDefns = acceptList(functionDefns, v);
		if(newUseStmts != useStmts || newFunctionDefns != functionDefns) {
			return new Program(useStmts, functionDefns);
		} else {
			return this;
		}	
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		// TODO Auto-generated method stub
		return null;
	}
}
