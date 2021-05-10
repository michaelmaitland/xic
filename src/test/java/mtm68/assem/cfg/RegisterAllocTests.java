package mtm68.assem.cfg;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import mtm68.assem.Assem;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;
import static mtm68.assem.AssemTestUtils.*;

public class RegisterAllocTests {
	
	private static final Set<RealReg> COLORS = SetUtils.elems(
			RealReg.R8,
			RealReg.R9,
			RealReg.R10,
			RealReg.R11,
			RealReg.R12,
			RealReg.R13,
			RealReg.R14,
			RealReg.R15,
			RealReg.RAX,
			RealReg.RBX,
			RealReg.RCX,
			RealReg.RDX,
			RealReg.RDI
		);
	
	@Test
	void registerAlloc() {
		RegisterAllocation regAlloc = new RegisterAllocation(COLORS);
		
		
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("t1"), reg("t2")),
				mov(reg("t2"), reg("t3"))
			);
		
		regAlloc.doRegisterAllocation(assems);
		
		System.out.println("Color map: " + regAlloc.getColorMap());
	}

}
