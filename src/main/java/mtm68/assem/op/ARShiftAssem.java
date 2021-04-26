package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class ARShiftAssem extends OperAssem {

	public ARShiftAssem(Dest dest, Src src) {
		super("sar", dest, src);
	}

}
