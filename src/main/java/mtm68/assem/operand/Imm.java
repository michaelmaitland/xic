package mtm68.assem.operand;

import java.util.List;

import mtm68.assem.HasRegs;
import mtm68.assem.pattern.PatternMatch;
import mtm68.util.ArrayUtils;

public class Imm extends AssemOp implements Ref, Src, PatternMatch{
	
	private long value;

	public Imm(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}

	public boolean is32Bit() {
		return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
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
