package mtm68.assem.operand;

import java.util.List;

import mtm68.assem.HasRegs;
import mtm68.util.ArrayUtils;

public class AbstractReg extends Reg {
	
	public AbstractReg(String id) {
		this.id = id;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.singleton(this);
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		return toSet.get(0);
	}
}
