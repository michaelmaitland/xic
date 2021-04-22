package mtm68.assem.op;

import mtm68.assem.OperAssem;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class AddAssem extends OperAssem {

	public AddAssem(Dest dest, Src src) {
		super("add", dest, src);
	}
	
	@Override
	public String toString() {
		return "add " + dest + ", " + src;
	}
}
