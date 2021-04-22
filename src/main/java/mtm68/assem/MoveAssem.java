package mtm68.assem;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class MoveAssem extends TwoOpAssem{
	
	public MoveAssem(Dest dest, Src src) {
		super(dest, src);
	}
	
	@Override
	public String toString() {
		return "mov " + dest + ", " + src;
	}
}
