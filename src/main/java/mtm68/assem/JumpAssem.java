package mtm68.assem;

import mtm68.assem.operand.Loc;

public class JumpAssem extends OneOpAssem{
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
		JMP,
		JE;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
}
