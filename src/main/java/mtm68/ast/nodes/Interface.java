package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public class Interface extends Node implements Root {
	
	private List<FunctionDecl> fDecls;

	public Interface(List<FunctionDecl> fDecls) {
		this.fDecls = fDecls;
	}

	@Override
	public String toString() {
		return "Interface [fDecls=" + fDecls + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startUnifiedList();
		p.startUnifiedList();

		for(FunctionDecl fDecl : fDecls) {
			p.startList();
			fDecl.prettyPrint(p);
			p.endList();
		}
		
		p.endList();
		p.endList();
	}
}
