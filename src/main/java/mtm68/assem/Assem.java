package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public abstract class Assem implements Cloneable, HasRegs, HasMutatableRegs {
	private String assem;

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		/* Do nothing since call has no regs */
		return this;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
	
	@Override
	public List<AbstractReg> getMutatedAbstractRegs() {
		return ArrayUtils.empty();
	}
	
	@SuppressWarnings("unchecked")
	public <A extends Assem> A copy() {
		try {
			return (A) clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
