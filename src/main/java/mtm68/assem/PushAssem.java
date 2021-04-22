package mtm68.assem;

import mtm68.assem.operand.Reg;

public class PushAssem extends OneOpAssem {

	public PushAssem(Reg reg) {
		super(reg);
	}
	
	@Override
	public String toString() {
		return "push " + reg;
	}
}
