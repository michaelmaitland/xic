package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
public interface HasMutatableRegs {

	/** 
	 * @return a list of the abstract registers that the instruction mutates.
	 */
	public List<AbstractReg> getMutatedAbstractRegs();
	
}
