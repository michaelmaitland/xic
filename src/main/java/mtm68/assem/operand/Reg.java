package mtm68.assem.operand;

import mtm68.assem.HasRegs;
import mtm68.util.Constants;
import mtm68.assem.pattern.PatternMatch;

public abstract class Reg extends AssemOp implements HasRegs, Ref, Acc, Src, Dest, PatternMatch {

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
