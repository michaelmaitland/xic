package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class AndAssem extends OperAssem {

	public AndAssem(Dest dest, Src src) {
		super("and", dest, src);
	}

}
