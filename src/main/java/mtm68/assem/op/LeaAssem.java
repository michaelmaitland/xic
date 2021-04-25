package mtm68.assem.op;

import java.util.List;

import mtm68.assem.HasRegs;
import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Src;

public class LeaAssem extends OperAssem{
	
	public LeaAssem(Dest dest, Src src) {
		super("lea", dest, src);
	}

	@Override
	public String toString() {
		return "lea " + dest + ", " + src;
	}
}
