package mtm68.ast.nodes;

import edu.cornell.cs.cs4120.util.SExpPrinter;

public interface LHS extends IExpr {
	public void prettyPrint(SExpPrinter p);
}