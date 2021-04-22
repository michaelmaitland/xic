package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.util.ArrayUtils;

public class CallAssem extends OneOpAssem {

	private String name;

	public CallAssem(String name) {
		super(null);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "call " + name;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
}
