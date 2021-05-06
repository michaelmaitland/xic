package mtm68.assem.operand;

import mtm68.util.FreshTempGenerator;

/**
 * Class for generating fresh registers.
 * 
 * @author Scott
 */
public class FreshRegGenerator {
	
	/**
	 * Get a fresh abstract reg whose name comes from FreshTempGenerator.getFreshTemp.
	 * 
	 * @return
	 */
	public static AbstractReg getFreshAbstractReg() {
		return new AbstractReg(FreshTempGenerator.getFreshTemp());
	}
}
