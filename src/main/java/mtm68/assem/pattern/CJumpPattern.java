package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;

/**
 * Pattern for IRCjump
 * 
 * @author Scott
 *
 */
public class CJumpPattern implements Pattern {

	private Pattern conditionPattern;

	public CJumpPattern(Pattern conditionPattern) {
		super();
		this.conditionPattern = conditionPattern;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRCJump)) return false;
		
		return conditionPattern.matches(((IRCJump)node).cond());
	}

	@Override
	public void addMatchedExprs(Map<String, IRExpr> exprs) {
		conditionPattern.addMatchedExprs(exprs);
	}

}
