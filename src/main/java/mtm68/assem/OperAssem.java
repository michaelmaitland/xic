package mtm68.assem;

import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Src;

public abstract class OperAssem extends TwoOpAssem {
	protected String name;
	protected Dest dest;
	protected Src src;

	public OperAssem(String name, Dest dest, Src src) {
		super(dest,src);
		this.name = name;
	}

	public Dest getDest() {
		return dest;
	}

	public Src getSrc() {
		return src;
	}

	@Override
	public String toString() {
		return name + " " + dest + ", " + src;
	}
}
