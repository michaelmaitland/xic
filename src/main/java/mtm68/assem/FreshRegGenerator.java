package mtm68.assem;

import mtm68.assem.operand.AbstractReg;
import mtm68.util.FreshTempGenerator;

public class FreshRegGenerator {
	
	public static AbstractReg getFreshAbstractReg() {
		return new AbstractReg(FreshTempGenerator.getFreshTemp());
	}
}
