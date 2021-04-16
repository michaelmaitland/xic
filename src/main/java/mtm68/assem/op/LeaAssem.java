package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class LeaAssem extends OperAssem{
	
	public LeaAssem(Dest dest, Src src) {
		super(dest, src);
	}
	
	@Override
	public String toString() {
		return "lea " + dest + ", " + src;
	}
}
