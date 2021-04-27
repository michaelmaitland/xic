package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.Dest;
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
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.concat(
				ReplaceableReg.fromDest(dest, this::setDest), 
				ReplaceableReg.fromSrc(src, this::setSrc));
	}
}
