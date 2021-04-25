package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
public interface HasRegs {

	
	/** 
	 * @return a list of the abstract registers.
	 */
	public List<AbstractReg> getAbstractRegs();
	
	
	/**
	 * Sets the abstract registers defined by getAbstractRegs()
	 * in the same order that getAbstractRegs() returns.
	 * @param toSet list ofreal registers to set. 
	 */
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet);


}
