package mtm68.assem.pattern;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRNode;

public class MemPattern implements Pattern {
	
	private Pattern operandPattern;
	
	public MemPattern(Pattern operandPattern) {
		this.operandPattern = operandPattern;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRMem)) return false;

		return operandPattern.matches(((IRMem)node).expr());
	}

	@Override
	public List<PatternMatch> getPatternMatches() {
		return operandPattern.getPatternMatches();
	}

}
