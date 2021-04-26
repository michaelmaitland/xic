package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class IMulAssem extends OperAssem {

	public IMulAssem(Dest dest, Src src) {
		super("imul", dest, src);
	}

}
