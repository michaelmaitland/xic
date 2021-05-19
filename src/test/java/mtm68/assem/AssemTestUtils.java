package mtm68.assem;

import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Loc;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;

public class AssemTestUtils {
	
	public static AbstractReg reg(String name) {
		return new AbstractReg(name);
	}
	
	public static MoveAssem mov(Dest dest, Src src) {
		return new MoveAssem(dest, src);
	}
	
	public static JumpAssem jmp(String loc) {
		return new JumpAssem(JumpType.JMP, new Loc(loc));
	}

	public static JumpAssem jmp(String loc, JumpType jumpType) {
		return new JumpAssem(jumpType, new Loc(loc));
	}

	public static LabelAssem label(String name) {
		return new LabelAssem(name);
	}
	
	public static RetAssem ret() {
		return new RetAssem();
	}

	public static Mem mem(Reg base) {
		return new Mem(base);
	}

	public static Imm imm(long val) {
		return new Imm(val);
	}

	public static Imm imm(int val) {
		return new Imm(val);
	}
}
