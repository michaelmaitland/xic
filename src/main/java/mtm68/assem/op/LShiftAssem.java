package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class LShiftAssem extends OperAssem {

	public LShiftAssem(Dest dest, Src src) {
		super("shl", dest, src);
	}

}
