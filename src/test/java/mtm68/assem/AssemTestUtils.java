package mtm68.assem;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public class AssemTestUtils {
	
	public static AbstractReg reg(String name) {
		return new AbstractReg(name);
	}
	
	public static MoveAssem mov(Dest dest, Src src) {
		return new MoveAssem(dest, src);
	}

}
