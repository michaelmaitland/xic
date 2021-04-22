package mtm68.assem.pattern;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRNode;

public interface Pattern {
	
	boolean matches(IRNode node);
	
	List<PatternMatch> getPatternMatches();

}