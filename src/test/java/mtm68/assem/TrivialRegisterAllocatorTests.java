package mtm68.assem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.op.AddAssem;
import mtm68.assem.op.LeaAssem;
import mtm68.assem.op.SubAssem;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Loc;
import mtm68.assem.operand.RealReg;
import mtm68.assem.visit.TrivialRegisterAllocator;

public class TrivialRegisterAllocatorTests {
	
	@Test
	public void testNoAbstr() {
		List<Assem> insts = allocateSingleFunc(
				new CallAssem("g"),
				//new CmpAssem(),
				new JumpAssem(JumpType.JMP, loc("header")),
				new LabelAssem("lbl"),
				new MoveAssem(RealReg.RAX, RealReg.RBX),
				new PushAssem(RealReg.RAX),
				//new TestAssem(),
				new AddAssem(RealReg.RAX, RealReg.RBX),
				new LeaAssem(RealReg.RAX, RealReg.RBX),
				new SubAssem(RealReg.RAX, RealReg.RBX)
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	// -----------------------------------------------------------------
	// OneOpInst
	// -----------------------------------------------------------------
	@Test
	public void testOneOpAssemRealReg() {
		List<Assem> insts = allocateSingleFunc(
				new PushAssem(RealReg.RCX)
				);
		assertAllRealReg(insts);
		printInsts(insts);	
	}
	
	@Test
	public void testOneOpAssemAllRealReg() {
		List<Assem> insts = allocateSingleFunc(
				new PushAssem(RealReg.RCX),
				new PushAssem(RealReg.RCX),
				new PushAssem(RealReg.RBP)
				);
		assertAllRealReg(insts);
		printInsts(insts);	
	}
	
	@Test
	public void testOneOpAssemAbstrReg() {
		List<Assem> insts = allocateSingleFunc(
				new PushAssem(abstrReg("t"))
				);
		assertAllRealReg(insts);
		printInsts(insts);	
	}
	
	// -----------------------------------------------------------------
	// TwoOpInst
	// -----------------------------------------------------------------

	@Test
	public void testTwoOpAssemNoAbstr() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(RealReg.RAX, RealReg.RBX)
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}

	@Test
	public void testTwoOpAssemOneAbstr() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), RealReg.RBX)
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testTwoOpAssemOneAbstr2() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(RealReg.RBX, abstrReg("t1"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}	
	
	@Test
	public void testTwoOpAssemTwoAbstrDifferent() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t2"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testTwoOpAssemTwoAbstrSame() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t1"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}

	@Test
	public void testTwoOpAssemOneAbstrMultipleInst() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), RealReg.RBX),
				new SubAssem(RealReg.RAX, abstrReg("t1"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testTwoOpAssemOneAbstrMultipleInst2() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t1")),
				new SubAssem(RealReg.RAX, abstrReg("t1"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testTwoOpAssemOneAbstrMultipleInst3() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t1")),
				new SubAssem(abstrReg("t1"), abstrReg("t1"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testTwoOpAssemTwoAbstrMultipleInst() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), RealReg.RBX),
				new SubAssem(RealReg.RAX, abstrReg("t2"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testTwoOpAssemTwoAbstrMultipleInst2() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t2")),
				new SubAssem(RealReg.RAX, abstrReg("t2"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
		
	@Test
	public void testTwoOpAssemTwoAbstrMultipleInst3() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t2")),
				new SubAssem(abstrReg("t2"), abstrReg("t1"))
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	@Test
	public void testMultipleInstsSameAbstr() {
		
	}

	@Test
	public void testMultiFunc() {
		
	}
	
	private void assertAllRealReg(List<Assem> insts) {
		for(Assem inst : insts) {
			assertEquals(0, inst.getAbstractRegs().size());
		}
	}

	private AbstractReg abstrReg(String id) {
		return new AbstractReg(id);
	}

	private Loc loc(String name) {
		return new Loc(name);
	}

	private List<Assem> allocateSingleFunc(Assem...assems) {
		SeqAssem seq = new SeqAssem(assems);
		FuncDefnAssem func = new FuncDefnAssem("f", seq);
		
		List<FuncDefnAssem> funcs = new ArrayList<>();
		funcs.add(func);
		CompUnitAssem comp = new CompUnitAssem("test.xi", funcs);

		return allocate(comp);
	}

	private List<Assem> allocate(CompUnitAssem a) {
		TrivialRegisterAllocator allocator = new TrivialRegisterAllocator();
		List<Assem> result = allocator.allocate(a);
		return result;
	}

	private void printInsts(List<Assem> insts) {
		insts.stream().forEach(System.out::println);
	}
}
