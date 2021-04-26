package mtm68.assem;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class CmpAssem extends OperAssem {

	public CmpAssem(Dest dest, Src src) {
		super("cmp", dest, src);
	}
}
