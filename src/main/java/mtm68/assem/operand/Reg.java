package mtm68.assem.operand;

import mtm68.assem.HasRegs;
import mtm68.util.Constants;

public abstract class Reg implements HasRegs, Ref, Acc, Src, Dest {
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
