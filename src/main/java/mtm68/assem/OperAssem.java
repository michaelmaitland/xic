package mtm68.assem;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;

public class OperAssem extends TwoOpAssem{
	protected Dest dest;
	protected Src src;
	
	public OperAssem(Dest dest, Src src) {
		this.dest = dest;
		this.src = src;
	}

	public Dest getDest() {
		return dest;
	}
	
	public Src getSrc() {
		return src;
	}

}
