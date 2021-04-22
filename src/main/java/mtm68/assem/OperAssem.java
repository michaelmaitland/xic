package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public abstract class OperAssem extends Assem {
	protected String name;
	protected Dest dest;
	protected Src src;

	public OperAssem(String name, Dest dest, Src src) {
		this.name = name;
		this.dest = dest;
		this.src = src;
	}

	public Dest getDest() {
		return dest;
	}

	public Src getSrc() {
		return src;
	}

	@Override
	public String toString() {
		return name + " " + dest + ", " + src;
	}
	
	@Override
	public List<AbstractReg> getAbstractRegs() {
		List<AbstractReg> destRegs = dest.getAbstractRegs();
		return ArrayUtils.concat(destRegs, src.getAbstractRegs());
	}	
}
