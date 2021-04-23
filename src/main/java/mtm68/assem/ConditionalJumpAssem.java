package mtm68.assem;

import mtm68.assem.operand.Loc;

public abstract class ConditionalJumpAssem extends Assem {
	
	protected String name;
	protected Loc loc;
	
	public ConditionalJumpAssem(String name, Loc loc) {
		super();
		this.name = name;
		this.loc = loc;
	}

	@Override
	public String toString() {
		return name + " " + loc;
	}

}
