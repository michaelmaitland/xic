package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Loc;
import mtm68.util.ArrayUtils;

public class JumpAssem extends OneOpAssem {
	private JumpType type;
	private Loc loc;

	public JumpAssem(JumpType type, Loc loc) {
		super(null);
		this.type = type;
		this.loc = loc;
	}
	
	@Override
	public String toString() {
		return type + " " + loc;
	}
	
	public enum JumpType{
		JMP;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
}
