package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;

/**
 * Represents a pattern that an IRNode can match
 * 
 * @author Scott
 */
public interface Pattern {
	
	/**
	 * Returns true if the given IRNode matches this pattern.
	 * @param node
	 * @return
	 */
	boolean matches(IRNode node);
	
	/**
	 * Adds recursively matched expressions to the map using
	 * the names provided as keys.
	 * 
	 * @param exprs
	 */
	void addMatchedExprs(Map<String, IRExpr> exprs);

}