package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;

public class IndexPattern extends MatchablePattern {
	
	public IndexPattern(String name) {
		super(name);
	}
	
	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRConst)) return false;
		
		matched = (IRConst) node;
		long value = matched.constant();

		return value == 1L || value == 2L || value == 4L || value == 8L;
	}

}
