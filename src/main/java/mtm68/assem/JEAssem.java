package mtm68.assem;

import mtm68.assem.operand.Loc;

public class JEAssem extends ConditionalJumpAssem {
	
	
	public JEAssem(Loc loc) {
		super("je", loc);
	}
}
