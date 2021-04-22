package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.util.ArrayUtils;

public class LabelAssem extends OneOpAssem {
	private String name;
	
	public LabelAssem(String name) {
		super(null);
		this.name = name;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
	
	@Override
	public String toString() {
		return name + ":";
	}
}
