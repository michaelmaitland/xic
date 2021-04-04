package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class Interface extends Node implements Root {
	
	private List<FunctionDecl> functionDecls;

	public Interface(List<FunctionDecl> fDecls) {
		this.functionDecls = fDecls;
	}

	public List<FunctionDecl> getFunctionDecls() {
		return functionDecls;
	}

	@Override
	public String toString() {
		return "Interface [fDecls=" + functionDecls + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();
		p.startUnifiedList();

		for(FunctionDecl fDecl : functionDecls) {
			p.startList();
			fDecl.prettyPrint(p);
			p.endList();
		}
		
		p.endList();
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		List<FunctionDecl> newFunctionDecls = acceptList(functionDecls, v);

		if(newFunctionDecls != functionDecls) {
			Interface i = copy();
			i.functionDecls = newFunctionDecls;
			return i;
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv,  IRNodeFactory irFactory) {
		/* There is no IR Node for an interface */
		return this;
	}
}
