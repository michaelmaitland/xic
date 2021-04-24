package mtm68.assem.operand;

import java.util.List;

import mtm68.assem.HasRegs;

public class Mem extends AssemOp implements Acc, Src, Dest {
	private Reg base;
	private Reg index;
	private int scale;
	private int disp;
	
	public Mem(Reg base, Reg index, int scale, int disp) {
		this.base = base;
		this.index = index;
		this.scale = scale;
		this.disp = disp;
	}

	public Mem(Reg base, Reg index, int scale) {
		this.base = base;
		this.index = index;
		this.scale = scale;
	}

	public Mem(Reg base, Reg index) {
		this.base = base;
		this.index = index;
	}

	public Mem(Reg base) {
		this.base = base;
	}
	
	public Mem(Reg base, int disp) {
		this.base = base;
		this.disp = disp;
	}
	
	public Reg getBase() {
		return base;
	}

	public void setBase(Reg base) {
		this.base = base;
	}

	public Reg getIndex() {
		return index;
	}

	public void setIndex(Reg index) {
		this.index = index;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getDisp() {
		return disp;
	}

	public void setDisp(int disp) {
		this.disp = disp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(base);
		
		if(index != null) {
			sb.append(" + ");
			if(scale != 0) sb.append(scale + " * ");
			sb.append(index);
		}
		
		if(disp != 0) sb.append(" + " + disp);
		
		sb.append("]");
		
		return sb.toString();
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		List<AbstractReg> regs = base.getAbstractRegs();
		
		if(index != null) {
			regs.addAll(index.getAbstractRegs());
		}

		return regs;
	}

	@Override
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		int numBase = base.getAbstractRegs().size();
		
		Reg newBase = (Reg)base.copyAndSetRealRegs(toSet.subList(0, numBase));
		
		Reg newIndex;
		if(index != null) {
			newIndex = (Reg)index.copyAndSetRealRegs(toSet.subList(numBase, toSet.size()));
		} else {
			newIndex = index;
		}
		
		Mem newMem = copy();
		newMem.setBase(newBase);
		newMem.setIndex(newIndex);
		return newMem;
	}
}
