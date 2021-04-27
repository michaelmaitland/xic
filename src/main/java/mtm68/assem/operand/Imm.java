package mtm68.assem.operand;

import mtm68.assem.pattern.PatternMatch;

public class Imm extends AssemOp implements Ref, Src, PatternMatch{
	
	private long value;

	public Imm(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}

	public boolean is32Bit() {
		return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
}
