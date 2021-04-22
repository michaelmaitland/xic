package mtm68.assem.pattern;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;
import mtm68.util.ArrayUtils;

public class VarPattern implements Pattern {
	
	private IRExpr expr;

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRExpr)) return false;
		
		expr = (IRExpr) node;

		return true;
	}

	@Override
	public List<PatternMatch> getPatternMatches() {
		return ArrayUtils.singleton(expr);
	}
}
