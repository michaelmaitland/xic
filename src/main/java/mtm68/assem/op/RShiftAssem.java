package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class RShiftAssem extends OperAssem {

	public RShiftAssem(Dest dest, Src src) {
		super("shr", dest, src);
	}

}
