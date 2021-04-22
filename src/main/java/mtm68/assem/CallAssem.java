package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class CallAssem extends Assem {

	private String name;

	public CallAssem(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "call " + name;
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		/* Do nothing since call has no regs */
		return this;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
}
