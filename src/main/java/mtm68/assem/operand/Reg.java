package mtm68.assem.operand;

import mtm68.assem.pattern.PatternMatch;
import mtm68.util.Constants;

public abstract class Reg extends AssemOp implements Ref, Acc, Src, Dest, PatternMatch {

	protected String id;

	public String getId() {
		return id;
	}
	
	public boolean isResultReg() {
		return id.startsWith(Constants.RET_PREFIX);
	}
	
	@Override
	public String toString() {
		return id;
	}

}
