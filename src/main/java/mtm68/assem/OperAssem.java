package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.RealReg;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDest(Dest dest) {
		this.dest = dest;
	}

	public void setSrc(Src src) {
		this.src = src;
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

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		int numDestRegs = dest.getAbstractRegs().size();
		Dest newDest = (Dest)dest.copyAndSetRealRegs(toSet.subList(0, numDestRegs));
		Src newSrc = (Src)src.copyAndSetRealRegs(toSet.subList(numDestRegs, toSet.size()));
		
		OperAssem newAssem = copy();
		newAssem.setDest(newDest);
		newAssem.setSrc(newSrc);
		
		return newAssem;
	}
}
