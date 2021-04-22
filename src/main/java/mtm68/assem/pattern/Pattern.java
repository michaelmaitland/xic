package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;

public interface Pattern {
	
	boolean matches(IRNode node);
	
	void addMatchedExprs(Map<String, IRExpr> exprs);

}