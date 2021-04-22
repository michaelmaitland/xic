package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public abstract class TwoOpAssem extends Assem {
	protected Dest dest;
	protected Src src;
	
	public TwoOpAssem(Dest dest, Src src) {
		super();
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
	public List<AbstractReg> getAbstractRegs() {
		List<AbstractReg> regs = ArrayUtils.empty();
		
		if(dest.isReg()) {
			Reg destReg = dest.getReg();
			if(destReg instanceof AbstractReg) {
				regs.add((AbstractReg)destReg);
			}
		}
		
		if (src.isReg()) {
			Reg srcReg = src.getReg();
			if (srcReg instanceof AbstractReg) {
				regs.add((AbstractReg)srcReg);
			}
		}

		return regs;
	}
}
