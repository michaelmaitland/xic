package mtm68.assem.pattern;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;
import mtm68.assem.operand.Imm;
import mtm68.util.ArrayUtils;

public class ConstantPattern implements Pattern {
	
	private long value;
	private boolean anyMatch;

	public ConstantPattern(long value) {
		this.value = value;
		anyMatch = false;
	}

	public ConstantPattern() {
		this.value = 0L;
		anyMatch = true;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRConst)) return false;
		
		IRConst c = (IRConst) node;
		
		if(anyMatch) {
			value = c.constant();
			return true;
		} 

		return c.constant() == value;
	}

	@Override
	public List<PatternMatch> getPatternMatches() {
		return ArrayUtils.singleton(new Imm(value));
	}
}
