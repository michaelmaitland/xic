package mtm68.assem.operand;

import java.util.List;
import java.util.stream.Collectors;

import mtm68.util.ArrayUtils;

public class RealReg extends Reg{

	public static final RealReg RAX = new RealReg(RealRegId.RAX);
	public static final RealReg RBX = new RealReg(RealRegId.RBX);
	public static final RealReg RCX = new RealReg(RealRegId.RCX);
	public static final RealReg RDX = new RealReg(RealRegId.RDX);
	public static final RealReg RSI = new RealReg(RealRegId.RSI);
	public static final RealReg RDI = new RealReg(RealRegId.RDI);
	public static final RealReg RSP = new RealReg(RealRegId.RSP);
	public static final RealReg RBP = new RealReg(RealRegId.RBP);
	public static final RealReg R8 = new RealReg(RealRegId.R8);
	public static final RealReg R9 = new RealReg(RealRegId.R9);
	public static final RealReg R10 = new RealReg(RealRegId.R10);
	public static final RealReg R11 = new RealReg(RealRegId.R11);
	public static final RealReg R12 = new RealReg(RealRegId.R12);
	public static final RealReg R13 = new RealReg(RealRegId.R13);
	public static final RealReg R14 = new RealReg(RealRegId.R14);
	public static final RealReg R15 = new RealReg(RealRegId.R15);

	public static final List<RealReg> getCallerSaveReg() {
		return ArrayUtils.elems(RAX, RCX, RDX, RSI, RDI, R8, R9, R10, R11);
	}

	public static final List<RealReg> getCalleeSaveReg() {
		return ArrayUtils.elems(RBP, RSP, RBX, R12, R13, R14, R15);
	}

	public RealReg(RealRegId regId) {
		this.id = regId.toString();
	}
	
	public enum RealRegId{
		RAX,
		RBX,
		RCX,
		RDX,
		RSI,
		RDI,
		RSP,
		RBP,
		R8,
		R9,
		R10,
		R11,
		R12,
		R13,
		R14,
		R15;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
		
		public static List<RealRegId> getArgRegIds() {
			return ArrayUtils.elems(RDI, RSI, RDX, RCX, R8, R9);
		}
		
		public static List<RealRegId> getCallerSaveRegIds() {
			return ArrayUtils.elems(RAX, RCX, RDX, RSI, RDI, R8, R9, R10, R11);
		}

		public static List<RealRegId> getCalleeSaveRegIds() {
			return ArrayUtils.elems(RBP, RSP, RBX, R12, R13, R14, R15);
		}

		public static List<RealReg> getArgRegs() {
			return getArgRegIds().stream()
					.map(RealReg::new)
					.collect(Collectors.toList());
		}
	}

}
