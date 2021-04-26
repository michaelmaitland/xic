package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class OrAssem extends OperAssem {

	public OrAssem(Dest dest, Src src) {
		super("or", dest, src);
	}

}
