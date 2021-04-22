package mtm68.assem.pattern;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;
import mtm68.assem.operand.Imm;
import mtm68.util.ArrayUtils;

public class IndexPattern implements Pattern {
	
	private long value;
	
	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRConst)) return false;
		
		IRConst constant = (IRConst) node;
		value = constant.constant();

		return value == 1L || value == 2L || value == 4L || value == 8L;
	}

	@Override
	public List<PatternMatch> getPatternMatches() {
		return ArrayUtils.singleton(new Imm(value));
	}

}
