package mtm68.assem;

import mtm68.assem.operand.Loc;

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
	
	public Loc getLoc() {
		return loc;
	}
	
	public JumpType getJumpType() {
		return type;
	}
	
	public boolean isUnconditional() {
		return type == JumpType.JMP; 
	}
	
	public enum JumpType{
		JMP,
		JE,
		JL,
		JNE,
		JG,
		JGE,
		JLE,
		JZ;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}
}
