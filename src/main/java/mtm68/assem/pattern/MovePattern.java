package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNode;

public class MovePattern implements Pattern {
	
	private Pattern destPattern;
	private Pattern srcPattern;
	
	public MovePattern(Pattern destPattern, Pattern srcPattern) {
		this.destPattern = destPattern;
		this.srcPattern = srcPattern;
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRMove)) return false;
		
		IRMove mov = (IRMove)node;
		
		return destPattern.matches(mov.target()) && srcPattern.matches(mov.source());
	}

	@Override
	public void addMatchedExprs(Map<String, IRExpr> exprs) {
		destPattern.addMatchedExprs(exprs);
		srcPattern.addMatchedExprs(exprs);
	}

}
