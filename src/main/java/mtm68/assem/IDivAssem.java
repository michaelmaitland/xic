package mtm68.assem;

import mtm68.assem.operand.Src;

public class IDivAssem extends Assem {
	
	private Src src;

	public IDivAssem(Src src) {
		super();
		this.src = src;
	}
	
	@Override
	public String toString() {
		return "idiv " + src;
	}

}
