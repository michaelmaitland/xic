package mtm68.assem.op;

import mtm68.assem.Assem;
import mtm68.assem.operand.Dest;

public class NegAssem extends Assem {
	
	private Dest dest;

	public NegAssem(Dest dest) {
		super();
		this.dest = dest;
	}
	
	@Override
	public String toString() {
		return "neg " + dest;
	}
	
	public Dest getDest() {
		return dest;
	}
}
