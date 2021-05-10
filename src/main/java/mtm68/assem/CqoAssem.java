package mtm68.assem;

import java.util.List;

import mtm68.assem.ReplaceableReg.RegType;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class CqoAssem extends Assem {

	public CqoAssem() {
		super();
	}
	
	@Override
	public String toString() {
		return "cqo";
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.elems(
				ReplaceableReg.fromRealReg(RealReg.RAX, RegType.READ),
				ReplaceableReg.fromRealReg(RealReg.RAX, RegType.WRITE),
				ReplaceableReg.fromRealReg(RealReg.RDX, RegType.WRITE)
			);
	}

}
