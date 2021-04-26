package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;

public abstract class MatchablePattern implements Pattern {
	
	protected String name;
	protected IRExpr matched;
	
	public MatchablePattern(String name) {
		this.name = name;
	}

	@Override
	public void addMatchedExprs(Map<String, IRExpr> exprs) {
		exprs.put(name, matched);
	}

}
