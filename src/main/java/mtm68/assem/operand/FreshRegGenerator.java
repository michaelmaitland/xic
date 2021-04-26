package mtm68.assem.operand;

import mtm68.util.FreshTempGenerator;

public class FreshRegGenerator {
	
	public static AbstractReg getFreshAbstractReg() {
		return new AbstractReg(FreshTempGenerator.getFreshTemp());
	}
}
