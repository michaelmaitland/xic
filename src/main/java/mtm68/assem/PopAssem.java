package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.Reg;

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
		return "pop " + reg;
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ReplaceableReg.fromDest(reg, this::setReg);
	}
}
