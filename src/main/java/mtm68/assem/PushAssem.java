package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.Reg;

public class PushAssem extends Assem {

	private Reg reg;

	public PushAssem(Reg reg) {
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
		return "push " + reg;
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ReplaceableReg.fromSrc(reg, this::setReg);
	}
}
