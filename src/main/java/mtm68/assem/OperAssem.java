package mtm68.assem;

import java.util.List;
import java.util.Set;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

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
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.concatMulti(
				ReplaceableReg.fromDest(dest, this::setDest),
				ReplaceableReg.fromSrc(src, this::setSrc),
				ReplaceableReg.fromSrc(dest, this::setDest));
	}
}
