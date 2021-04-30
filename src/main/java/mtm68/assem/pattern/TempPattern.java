package mtm68.assem.pattern;

import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRTemp;


/**
 * Pattern for IRTemp
 * 
 * @author Scott
 */
public class TempPattern extends MatchablePattern {

	public TempPattern(String name) {
		super(name);
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRTemp)) return false;
		
		matched = (IRTemp) node;
		
		return true;
	}

}
