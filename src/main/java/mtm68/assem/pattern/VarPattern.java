package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;

public class VarPattern extends MatchablePattern {
	
	public VarPattern(String name) {
		super(name);
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRExpr)) return false;
		
		matched = (IRExpr) node;

		return true;
	}
}
