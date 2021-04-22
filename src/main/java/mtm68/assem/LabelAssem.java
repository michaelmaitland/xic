package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class LabelAssem extends Assem {
	private String name;
	
	public LabelAssem(String name) {
		this.name = name;
	}

	
	@Override
	public String toString() {
		return name + ":";
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
	
	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		/* Do nothing since label has no regs */
		return this;	
	}
}
