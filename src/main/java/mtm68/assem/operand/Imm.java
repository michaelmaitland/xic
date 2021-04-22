package mtm68.assem.operand;

import java.util.List;

import mtm68.assem.HasRegs;
import mtm68.util.ArrayUtils;

public class Imm implements Ref, Src {
	
	private int value;

	public Imm(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		/* No real regs to set */
		return this;
	}

}
