package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.Src;

public class MulAssem extends Assem {
	
	private Src src;

	public MulAssem(Src src) {
		super();
		this.src = src;
	}
	
	public Src getSrc() {
		return src;
	}

	public void setSrc(Src src) {
		this.src = src;
	}

	@Override
	public String toString() {
		return "mul " + src;
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ReplaceableReg.fromSrc(src, this::setSrc);
	}
}
