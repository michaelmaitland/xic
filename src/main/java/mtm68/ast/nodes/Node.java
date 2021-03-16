package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public abstract class Node {
	
	public abstract void prettyPrint(SExpPrinter p);
	

	public <N extends Node> N visitChild(N n, Visitor v) {
		if(n == null) return null;
		else return v.visit(n);
	}

	public abstract Node visitChildren(Visitor v);

	public abstract Node typeCheck(TypeChecker tc);
}
