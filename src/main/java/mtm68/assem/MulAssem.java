package mtm68.assem;

import mtm68.assem.operand.Src;

public class MulAssem extends Assem {
	
	private Src src;

	public MulAssem(Src src) {
		super();
		this.src = src;
	}
	
	@Override
	public String toString() {
		return "mul " + src;
	}

}
