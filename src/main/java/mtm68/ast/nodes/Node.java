package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public abstract class Node {
	
	public abstract void prettyPrint(SExpPrinter p);
	
	public Node visit(Visitor v) {
		Visitor v2 = v.enter(this);
		Node n = visitChildren(v);
		return v2.leave(n, this);
	}

	public abstract Node visitChildren(Visitor v);

	public abstract Node typeCheck(TypeChecker tc);
}
