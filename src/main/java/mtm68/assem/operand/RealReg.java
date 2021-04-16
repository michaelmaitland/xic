package mtm68.assem.operand;

public class RealReg extends Reg{

	public RealReg(RealRegId regId) {
		this.id = regId.toString();
	}
	
	public enum RealRegId{
		RAX,
		RBX,
		RCX,
		RDX,
		ROI,
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
	}
}
