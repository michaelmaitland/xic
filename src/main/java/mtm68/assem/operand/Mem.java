package mtm68.assem.operand;

import java.util.List;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.assem.HasReplaceableRegs;
import mtm68.assem.ReplaceableReg;
import mtm68.util.ArrayUtils;

public class Mem extends AssemOp implements Acc, Src, Dest, HasReplaceableRegs {
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

	public Mem(Reg base, Reg index, Imm scale, Imm disp) {
		assertValidScale(scale);
		assert32Bit(disp);

		this.base = base;
		this.index = index;
		this.scale = (int)scale.getValue();
		this.disp = (int)disp.getValue();
	}

	public Mem(Reg base, Imm disp) {
		assert32Bit(disp);

		this.base = base;
		this.disp = (int)disp.getValue();
	}

	public Mem(Reg base, Reg index, int scale) {
		this.base = base;
		this.index = index;
		this.scale = scale;
	}

	public Mem(Reg base, Reg index, Imm scale) {
		assertValidScale(scale);

		this.base = base;
		this.index = index;
		this.scale = (int)scale.getValue();
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

	private void assert32Bit(Imm imm) {
		if(!imm.is32Bit()) throw new InternalCompilerError("Immediates in mem operand must be 32-bit");
	}

	private void assertValidScale(Imm imm) {
		long value = imm.getValue();
		boolean valid = value == 1L || value == 2L || value == 4L || value == 8L;
		if(!valid) throw new InternalCompilerError("Scale must be either 1, 2, 4, or 8. Got: " + value);
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
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.concat(
				ReplaceableReg.fromSrc(base, this::setBase),
				ReplaceableReg.fromSrc(index, this::setIndex));
	}
}
