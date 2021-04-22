package mtm68.assem;

import java.util.List;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Loc;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;

public class JumpAssem extends Assem {
	private JumpType type;
	private Loc loc;

	public JumpAssem(JumpType type, Loc loc) {
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
	public HasRegs copyAndSetRealRegs(List<RealReg> toSet) {
		/* Do nothing since jump has no regs */
		return this;
	}

	@Override
	public List<AbstractReg> getAbstractRegs() {
		return ArrayUtils.empty();
	}
}
