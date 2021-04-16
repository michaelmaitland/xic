package mtm68.assem;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class MoveAssem extends TwoOpAssem{
	private Dest dest;
	private Src src;
	
	public MoveAssem(Dest dest, Src src) {
		this.dest = dest;
		this.src = src;
	}
	
	@Override
	public String toString() {
		return "mov " + dest + ", " + src;
	}
}
