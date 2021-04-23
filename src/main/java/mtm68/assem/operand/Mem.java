package mtm68.assem.operand;

import polyglot.util.InternalCompilerError;

public class Mem implements Acc, Src, Dest {
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

	public Mem(Reg base, int disp) {
		this.base = base;
		this.disp = disp;
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
}
