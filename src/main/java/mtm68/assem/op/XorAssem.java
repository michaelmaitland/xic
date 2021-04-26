package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class XorAssem extends OperAssem {

	public XorAssem(Dest dest, Src src) {
		super("xor", dest, src);
	}

}
