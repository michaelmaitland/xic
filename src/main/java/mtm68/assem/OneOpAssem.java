package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Reg;
import mtm68.util.ArrayUtils;

public abstract class OneOpAssem extends Assem {

	protected Reg reg;

	public OneOpAssem(Reg reg) {
		super();
		this.reg = reg;
	}

	public Reg getReg() {
		return reg;
	}

	public void setReg(Reg reg) {
		this.reg = reg;
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
