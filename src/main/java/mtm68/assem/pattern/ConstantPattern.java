package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;

public class ConstantPattern extends MatchablePattern {
	
	private long value;
	private boolean anyMatch;

	public ConstantPattern(String name, long value) {
		super(name);
		this.value = value;
		anyMatch = false;
	}

	public ConstantPattern(String name) {
		super(name);
		this.value = 0L;
		anyMatch = true;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRConst)) return false;
		
		matched = (IRConst) node;
		
		if(anyMatch) {
			return true;
		} 

		return matched.constant() == value;
	}
}
