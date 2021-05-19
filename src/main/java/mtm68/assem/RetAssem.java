package mtm68.assem;

import java.util.Set;

import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.util.SetUtils;

public class RetAssem extends Assem{
	
	@Override
	public String toString() {
		return "ret";
	}
	
	@Override
	public Set<Reg> use() {
		return SetUtils.elems(RealReg.RAX, RealReg.RDX);
	}
}
