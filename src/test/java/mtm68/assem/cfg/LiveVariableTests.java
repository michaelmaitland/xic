package mtm68.assem.cfg;

import static mtm68.assem.AssemTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.assem.Assem;
import mtm68.assem.CmpAssem;
import mtm68.assem.CqoAssem;
import mtm68.assem.IDivAssem;
import mtm68.assem.MulAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.SetccAssem;
import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.SetccAssem.CC;
import mtm68.assem.op.AddAssem;
import mtm68.assem.operand.RealReg;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

public class LiveVariableTests {
	
	@Test
	void testSimple() throws IOException {
		Liveness liveness = new Liveness();
		
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("t1"), reg("t2")),
				mov(reg("t3"), reg("t2")),
				mov(reg("t2"), reg("t4")),
				mov(reg("t8"), reg("t5")),
				mov(reg("t9"), reg("t10"))
			);
		
		liveness.performLiveVariableAnalysis(assems);
		
		liveness.show(new PrintWriter(System.out));
	}

	@Test
	void testClass() throws IOException {
		Liveness liveness = new Liveness();
		
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("a"), imm(1)),
				label("header"),
				new CmpAssem(reg("a"), imm(0)),
				jmp("true", JumpType.JE),
				new RetAssem(),
				label("true"),
				mov(RealReg.RAX, reg("a")),
				new MulAssem(reg("a")),
				mov(reg("z"), RealReg.RAX),
				new AddAssem(reg("y"), reg("a")),
				new CmpAssem(reg("x"), imm(1)),
				jmp("alsoTrue", JumpType.JE),
				mov(reg("a"), reg("y")),
				jmp("header"),
				label("alsoTrue"),
				mov(reg("a"), reg("z")),
				jmp("header")
			);
		
		liveness.performLiveVariableAnalysis(assems);
		
		liveness.show(new PrintWriter(System.out));
	}
	
	@Test
	void moveUseDef() {
		Assem mov = mov(reg("t1"), reg("t2"));
		
		assertEquals(SetUtils.elems(reg("t1")), mov.def());
		assertEquals(SetUtils.elems(reg("t2")), mov.use());
	}

	@Test
	void moveUseDefMem() {
		Assem mov = mov(mem(reg("t1")), reg("t2"));
		
		assertEquals(SetUtils.empty(), mov.def());
		assertEquals(SetUtils.elems(reg("t1"), reg("t2")), mov.use());
	}

	@Test
	void divUseDef() {
		Assem div = new IDivAssem(reg("t"));
		
		assertEquals(SetUtils.elems(RealReg.RDX, RealReg.RAX), div.def());
		assertEquals(SetUtils.elems(RealReg.RAX, reg("t")), div.use());
	}

	@Test
	void cqoUseDef() {
		Assem cqo = new CqoAssem();
		
		assertEquals(SetUtils.elems(RealReg.RDX, RealReg.RAX), cqo.def());
		assertEquals(SetUtils.elems(RealReg.RAX), cqo.use());
	}

	@Test
	void mulUseDef() {
		Assem mul = new MulAssem(reg("t"));
		
		assertEquals(SetUtils.elems(RealReg.RDX, RealReg.RAX), mul.def());
		assertEquals(SetUtils.elems(RealReg.RAX, reg("t")), mul.use());
	}

	@Test
	void setccUseDef() {
		Assem setcc = new SetccAssem(CC.E);
		
		assertEquals(SetUtils.elems(RealReg.RAX), setcc.def());
		assertEquals(SetUtils.empty(), setcc.use());
	}

}
