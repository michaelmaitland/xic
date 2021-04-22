package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public class MoveAssem extends Assem {
	private Dest dest;
	private Src src;
	
	public MoveAssem(Dest dest, Src src) {
		this.dest = dest;
		this.src = src;
	}
	public Dest getDest() {
		return dest;
	}
	public void setDest(Dest dest) {
		this.dest = dest;
	}
	public Src getSrc() {
		return src;
	}
	public void setSrc(Src src) {
		this.src = src;
	}

	@Override
	public String toString() {
		return "mov " + dest + ", " + src;
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {


		int numDestRegs = dest.getAbstractRegs().size();
		Dest newDest = (Dest)dest.copyAndSetRealRegs(toSet.subList(0, numDestRegs));
		Src newSrc = (Src)src.copyAndSetRealRegs(toSet.subList(numDestRegs, toSet.size()));
		
		MoveAssem newMove = copy();
		newMove.setDest(newDest);
		newMove.setSrc(newSrc);
		
		return newMove;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		List<AbstractReg> destRegs = dest.getAbstractRegs();
		return ArrayUtils.concat(destRegs, src.getAbstractRegs());
	}
}
