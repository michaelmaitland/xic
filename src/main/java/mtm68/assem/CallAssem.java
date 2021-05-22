package mtm68.assem;

import java.util.List;
import java.util.Set;

import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.util.SetUtils;

public class CallAssem extends Assem {

	private String name;
	private int numArgs;

	public CallAssem(String name, int numArgs) {
		this.name = name;
		this.numArgs = numArgs;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNumArgs() {
		return numArgs;
	}
	
	@Override
	public String toString() {
		return "call " + name;
	}
	
	@Override
	public Set<Reg> use() {
		List<RealReg> argRegs = RealReg.getArgRegs();
		return SetUtils.fromList(argRegs.subList(0, Math.min(numArgs, argRegs.size())));
	}
	
	@Override
	public Set<Reg> def() {
		return SetUtils.fromList(RealReg.getCallerSaveReg());
	}
	
}
