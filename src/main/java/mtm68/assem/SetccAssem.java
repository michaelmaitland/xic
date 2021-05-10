package mtm68.assem;

import java.util.List;

import mtm68.assem.ReplaceableReg.RegType;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class SetccAssem extends Assem {
	
	private CC cc;
	
	public SetccAssem(CC cc) {
		this.cc = cc;
	}
	
	@Override
	public String toString() {
		return "set" + cc + " al";
	}
	
	@Override
	public List<ReplaceableReg> getReplaceableRegs() {
		return ArrayUtils.singleton(ReplaceableReg.fromRealReg(RealReg.RAX, RegType.WRITE));
	}
	
	public static enum CC {
		G,
		GE,
		L,
		LE,
		E,
		B,
		NE;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
