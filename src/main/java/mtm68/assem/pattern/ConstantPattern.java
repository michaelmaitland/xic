package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;

public abstract class ConstantPattern extends MatchablePattern {
	
	public ConstantPattern(String name) {
		super(name);
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRConst)) return false;
		
		matched = (IRConst) node;
		
		return valueMatches(matched.constant());
	}
	
	protected abstract boolean valueMatches(long value);
}
