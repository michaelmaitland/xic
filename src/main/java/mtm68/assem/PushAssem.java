package mtm68.assem;

import mtm68.assem.operand.Reg;

public class PushAssem extends OneOpAssem {
	
	private Reg reg;

	public PushAssem(Reg reg) {
		this.reg = reg;
	}
	
	@Override
	public String toString() {
		return "push " + reg;
	}

}
