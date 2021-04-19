package mtm68.assem.operand;

import java.util.List;
import java.util.stream.Collectors;

import mtm68.util.ArrayUtils;

public class RealReg extends Reg{
	
	public static final RealReg RSP = new RealReg(RealRegId.RSP);
	public static final RealReg RAX = new RealReg(RealRegId.RAX);
	public static final RealReg RDX = new RealReg(RealRegId.RDX);

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

		public static List<RealReg> getArgRegs() {
			return getArgRegIds().stream()
					.map(RealReg::new)
					.collect(Collectors.toList());
		}
	}
}
