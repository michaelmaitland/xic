package mtm68.assem;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;

public class OperAssem {

	Src src;
	Dest dest;
	
	public OperAssem(Src src, Dest dest) {
		assert (!(src instanceof Reg || dest instanceof Reg));
	}
	
}
