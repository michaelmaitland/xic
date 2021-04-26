package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.util.ArrayUtils;

public class PopAssem extends Assem {

	private Reg reg;

	public PopAssem(Reg reg) {
		this.reg = reg;
	}

	public Reg getReg() {
		return reg;
	}

	public void setReg(Reg reg) {
		this.reg = reg;
	}

	@Override
	public String toString() {
		return "pop" + reg;
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		Reg newReg = (Reg)reg.copyAndSetRealRegs(toSet);

		PopAssem newPush = copy();
		newPush.setReg(newReg);
		return newPush;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		if(reg instanceof AbstractReg) {
			return ArrayUtils.singleton((AbstractReg)reg);
		} else {
			return ArrayUtils.empty();
		}
	}
}
