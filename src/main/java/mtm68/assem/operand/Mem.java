package mtm68.assem.operand;

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
	public boolean isReg() {
		return true;
	}

	@Override
	public Reg getReg() {
		// TODO Auto-generated method stub
		return null;
	}
}
