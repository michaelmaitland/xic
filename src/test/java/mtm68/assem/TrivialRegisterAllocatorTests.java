package mtm68.assem;

import static mtm68.assem.operand.RealReg.R10;
import static mtm68.assem.operand.RealReg.R9;
import static mtm68.assem.operand.RealReg.RAX;
import static mtm68.assem.operand.RealReg.RBP;
import static mtm68.assem.operand.RealReg.RBX;
import static mtm68.assem.operand.RealReg.RCX;
import static mtm68.util.TestUtils.assertInstanceOf;
import static mtm68.util.TestUtils.assertInstanceOfAndReturn;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.op.AddAssem;
import mtm68.assem.op.LeaAssem;
import mtm68.assem.op.SubAssem;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Loc;
import mtm68.assem.operand.Mem;
import mtm68.assem.visit.TrivialRegisterAllocator;
import mtm68.util.ArrayUtils;

public class TrivialRegisterAllocatorTests {
	
	@Test
	public void testNoAbstr() {
		List<Assem> insts = allocateSingleFunc(
				new CallAssem("g"),
				new JumpAssem(JumpType.JMP, loc("header")),
				new LabelAssem("lbl"),
				new MoveAssem(RAX, RBX),
				new PushAssem(RAX),
				new AddAssem(RAX, RBX),
				new LeaAssem(RAX, RBX),
				new SubAssem(RAX, RBX)
				);
		assertAllRealReg(insts);
		printInsts(insts);
	}
	
	// -----------------------------------------------------------------
	// Mem Operand
	// -----------------------------------------------------------------
	@Test
	public void testMemJustBase() {
		List<Assem> insts = allocateSingleFunc(
				new MoveAssem(new Mem(abstrReg("t1")), new Imm(4))
				);
		assertAllRealReg(insts);
		printInsts(insts);
		MoveAssem m = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m.getDest());

		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
		assertInstanceOf(Mem.class, m2.getDest());
		
		assertEquals(2, insts.size());
	}
	
	// -----------------------------------------------------------------
	// IDiv
	// -----------------------------------------------------------------
	@Test
	public void testIDiv() {
		List<Assem> insts = allocateSingleFunc(new IDivAssem(abstrReg("t1")));
		assertAllRealReg(insts);
		printInsts(insts);

		MoveAssem m = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m.getDest());

		IDivAssem d = assertInstanceOfAndReturn(IDivAssem.class, insts.get(1));
		assertEquals(R9, d.getSrc());

		assertEquals(2, insts.size());
	}
	
	// -----------------------------------------------------------------
	// Mul
	// -----------------------------------------------------------------
	@Test
	public void testMul() {
		List<Assem> insts = allocateSingleFunc(new MulAssem(abstrReg("t1")));
		assertAllRealReg(insts);
		printInsts(insts);

		MoveAssem m = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m.getDest());

		MulAssem d = assertInstanceOfAndReturn(MulAssem.class, insts.get(1));
		assertEquals(R9, d.getSrc());

		assertEquals(2, insts.size());
	}
	
	// -----------------------------------------------------------------
	// Mem Operand
	// -----------------------------------------------------------------
	@Test
	public void testMemBaseAndIndex() {
		List<Assem> insts = allocateSingleFunc(new MoveAssem(new Mem(abstrReg("t1"), abstrReg("t2")), new Imm(4)));
		assertAllRealReg(insts);
		printInsts(insts);
		MoveAssem m = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m.getDest());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
		assertEquals(R10, m2.getDest());


		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertInstanceOf(Mem.class, m3.getDest());

		assertEquals(3, insts.size());
	}
	
	// -----------------------------------------------------------------
		// Mem Operand
		// -----------------------------------------------------------------
		@Test
		public void testMemBaseAndIndex2() {
			List<Assem> insts = allocateSingleFunc(new MoveAssem(new Mem(abstrReg("t1"), abstrReg("t1")), new Imm(4)));
			assertAllRealReg(insts);
			printInsts(insts);
			MoveAssem m = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
			assertEquals(R9, m.getDest());
			
			MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
			assertInstanceOf(Mem.class, m2.getDest());

			assertEquals(2, insts.size());
		}
	
	// -----------------------------------------------------------------
	// OneOpInst
	// -----------------------------------------------------------------
	@Test
	public void testOneOpAssemRealReg() {
		List<Assem> insts = allocateSingleFunc(
				new PushAssem(RCX)
				);
		assertAllRealReg(insts);
		PushAssem p = assertInstanceOfAndReturn(PushAssem.class, insts.get(0));
		assertEquals(RCX, p.getReg());
	}
	
	@Test
	public void testOneOpAssemAllRealReg() {
		List<Assem> insts = allocateSingleFunc(
				new PushAssem(RCX),
				new PushAssem(RBP)
				);
		assertAllRealReg(insts);
		PushAssem p0 = assertInstanceOfAndReturn(PushAssem.class, insts.get(0));
		assertEquals(RCX, p0.getReg());
		PushAssem p1 = assertInstanceOfAndReturn(PushAssem.class, insts.get(1));
		assertEquals(RBP, p1.getReg());
	}
	
	@Test
	public void testOneOpAssemAbstrReg() {
		List<Assem> insts = allocateSingleFunc(
				new PushAssem(abstrReg("t"))
				);

		assertAllRealReg(insts);
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());
		PushAssem p = assertInstanceOfAndReturn(PushAssem.class, insts.get(1));
		assertEquals(R9, p.getReg());
	}
	
	// -----------------------------------------------------------------
	// TwoOpInst
	// -----------------------------------------------------------------

	@Test
	public void testTwoOpAssemNoAbstr() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(RAX, RBX)
				);

		assertAllRealReg(insts);
		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(0));
		assertEquals(RAX, a.getDest());	
		assertEquals(RBX, a.getSrc());	
	}

	@Test
	public void testTwoOpAssemOneAbstr() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), RBX)
				);
		assertAllRealReg(insts);
		
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(R9, a.getDest());
		assertEquals(RBX, a.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertEquals(R9, m2.getSrc());
		assertInstanceOfAndReturn(Mem.class, m2.getDest());
	}
	
	@Test
	public void testTwoOpAssemOneAbstr2() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(RBX, abstrReg("t1"))
				);
		
		assertAllRealReg(insts);

		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(RBX, a.getDest());
		assertEquals(R9, a.getSrc());
	}	
	
	@Test
	public void testTwoOpAssemTwoAbstrDifferent() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t2"))
				);
		assertAllRealReg(insts);
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
		assertEquals(R10, m2.getDest());
		assertInstanceOfAndReturn(Mem.class, m2.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(2));
		assertEquals(R9, a.getDest());
		assertEquals(R10, a.getSrc());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getSrc());
		assertInstanceOfAndReturn(Mem.class, m3.getDest());

		assertEquals(4, insts.size());
	}
	
	@Test
	public void testTwoOpAssemTwoAbstrSame() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t1"))
				);
		assertAllRealReg(insts);
		
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(R9, a.getDest());
		assertEquals(R9, a.getSrc());
		
		assertEquals(3, insts.size());
	}

	@Test
	public void testTwoOpAssemOneAbstrMultipleInst() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), RBX),
				new SubAssem(RAX, abstrReg("t1"))
				);
		assertAllRealReg(insts);

		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(R9, a.getDest());
		assertEquals(RBX, a.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertEquals(R9, m2.getSrc());
		assertInstanceOfAndReturn(Mem.class, m2.getDest());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getDest());
		assertInstanceOfAndReturn(Mem.class, m3.getSrc());

		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(4));
		assertEquals(RAX, s.getDest());
		assertEquals(R9, s.getSrc());
		
		assertEquals(5, insts.size());
	}
	
	@Test
	public void testTwoOpAssemOneAbstrMultipleInst2() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t1")),
				new SubAssem(RAX, abstrReg("t1"))
				);

		assertAllRealReg(insts);
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(R9, a.getDest());
		assertEquals(R9, a.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertEquals(R9, m2.getSrc());
		assertInstanceOfAndReturn(Mem.class, m2.getDest());

		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getDest());
		assertInstanceOfAndReturn(Mem.class, m3.getSrc());

		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(4));
		assertEquals(RAX, s.getDest());
		assertEquals(R9, s.getSrc());
		
		assertEquals(5, insts.size());
	}
	
	@Test
	public void testTwoOpAssemOneAbstrMultipleInst3() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t1")),
				new SubAssem(abstrReg("t1"), abstrReg("t1"))
				);
		assertAllRealReg(insts);
		
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(R9, a.getDest());
		assertEquals(R9, a.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertEquals(R9, m2.getSrc());
		assertInstanceOfAndReturn(Mem.class, m2.getDest());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getDest());
		assertInstanceOfAndReturn(Mem.class, m3.getSrc());

		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(4));
		assertEquals(R9, s.getDest());
		assertEquals(R9, s.getSrc());
		
		MoveAssem m4 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(5));
		assertEquals(R9, m4.getSrc());
		assertInstanceOfAndReturn(Mem.class, m4.getDest());

		assertEquals(6, insts.size());
	}
	
	@Test
	public void testTwoOpAssemTwoAbstrMultipleInst() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), RBX),	
				new SubAssem(RAX, abstrReg("t2"))
				);
		
		assertAllRealReg(insts);
		
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(1));
		assertEquals(R9, a.getDest());
		assertEquals(RBX, a.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertEquals(R9, m2.getSrc());
		assertInstanceOfAndReturn(Mem.class, m2.getDest());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getDest());
		assertInstanceOfAndReturn(Mem.class, m3.getSrc());

		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(4));
		assertEquals(RAX, s.getDest());
		assertEquals(R9, s.getSrc());
		
		assertEquals(5, insts.size());
	}
	

	@Test
	public void testTwoOpAssemTwoAbstrMultipleInst2() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t2")),
				new SubAssem(RAX, abstrReg("t2"))
				);
		assertAllRealReg(insts);
		
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
		assertEquals(R10, m2.getDest());
		assertInstanceOfAndReturn(Mem.class, m2.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(2));
		assertEquals(R9, a.getDest());
		assertEquals(R10, a.getSrc());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getSrc());
		assertInstanceOfAndReturn(Mem.class, m3.getDest());

		MoveAssem m5 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(4));
		assertEquals(R9, m5.getDest());
		assertInstanceOfAndReturn(Mem.class, m5.getSrc());

		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(5));
		assertEquals(RAX, s.getDest());
		assertEquals(R9, s.getSrc());
		
		assertEquals(6, insts.size());
	}
		
	@Test
	public void testTwoOpAssemTwoAbstrMultipleInst3() {
		List<Assem> insts = allocateSingleFunc(
				new AddAssem(abstrReg("t1"), abstrReg("t2")),
				new SubAssem(abstrReg("t2"), abstrReg("t1"))
				);
		assertAllRealReg(insts);
		
		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(0));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
		assertEquals(R10, m2.getDest());
		assertInstanceOfAndReturn(Mem.class, m2.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(2));
		assertEquals(R9, a.getDest());
		assertEquals(R10, a.getSrc());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(3));
		assertEquals(R9, m3.getSrc());
		assertInstanceOfAndReturn(Mem.class, m3.getDest());

		MoveAssem m5 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(4));
		assertEquals(R9, m5.getDest());
		assertInstanceOfAndReturn(Mem.class, m5.getSrc());
		
		MoveAssem m6 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(5));
		assertEquals(R10, m6.getDest());
		assertInstanceOfAndReturn(Mem.class, m6.getSrc());

		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(6));
		assertEquals(R9, s.getDest());
		assertEquals(R10, s.getSrc());
		
		MoveAssem m7 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(7));
		assertEquals(R9, m7.getSrc());
		assertInstanceOfAndReturn(Mem.class, m7.getDest());

		assertEquals(8, insts.size());
	}
	
	@Test
	public void testMultiFunc() {
		SeqAssem seqF = new SeqAssem(
				new LabelAssem("f"),
				new AddAssem(abstrReg("t1"), abstrReg("t2")),
				new SubAssem(RAX, abstrReg("t2"))
				);

		SeqAssem seqG = new SeqAssem(
				new LabelAssem("g"),
				new AddAssem(abstrReg("t1"), RBX),
				new SubAssem(RAX, abstrReg("t1"))
				);
		
		FuncDefnAssem f = new FuncDefnAssem("f", seqF);
		FuncDefnAssem g = new FuncDefnAssem("g", seqG);
		List<FuncDefnAssem> funcs = ArrayUtils.elems(f, g);
		List<Assem> insts = allocateMultipleFuncs(funcs);
		
		LabelAssem l1 = assertInstanceOfAndReturn(LabelAssem.class, insts.get(0));
		assertEquals("f", l1.getName());

		MoveAssem m1 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(1));
		assertEquals(R9, m1.getDest());
		assertInstanceOfAndReturn(Mem.class, m1.getSrc());
		
		MoveAssem m2 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(2));
		assertEquals(R10, m2.getDest());
		assertInstanceOfAndReturn(Mem.class, m2.getSrc());

		AddAssem a = assertInstanceOfAndReturn(AddAssem.class, insts.get(3));
		assertEquals(R9, a.getDest());
		assertEquals(R10, a.getSrc());
		
		MoveAssem m3 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(4));
		assertEquals(R9, m3.getSrc());
		assertInstanceOfAndReturn(Mem.class, m3.getDest());

		MoveAssem m5 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(5));
		assertEquals(R9, m5.getDest());
		assertInstanceOfAndReturn(Mem.class, m5.getSrc());
		
		SubAssem s = assertInstanceOfAndReturn(SubAssem.class, insts.get(6));
		assertEquals(RAX, s.getDest());
		assertEquals(R9, s.getSrc());
		
		LabelAssem l2 = assertInstanceOfAndReturn(LabelAssem.class, insts.get(7));
		assertEquals("g", l2.getName());
	
		MoveAssem m9 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(8));
		assertEquals(R9, m9.getDest());
		assertInstanceOfAndReturn(Mem.class, m9.getSrc());

		AddAssem a2 = assertInstanceOfAndReturn(AddAssem.class, insts.get(9));
		assertEquals(R9, a2.getDest());
		assertEquals(RBX, a2.getSrc());
		
		MoveAssem m10 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(10));
		assertEquals(R9, m10.getSrc());
		assertInstanceOfAndReturn(Mem.class, m10.getDest());
		
		MoveAssem m11 = assertInstanceOfAndReturn(MoveAssem.class, insts.get(11));
		assertEquals(R9, m11.getDest());
		assertInstanceOfAndReturn(Mem.class, m11.getSrc());

		SubAssem s2 = assertInstanceOfAndReturn(SubAssem.class, insts.get(12));
		assertEquals(RAX, s2.getDest());
		assertEquals(R9, s2.getSrc());
		
	}
	
	
	
	
	private void assertAllRealReg(List<Assem> insts) {
		for(Assem inst : insts) {
			assertEquals(0, inst.getReplaceableRegs().size());
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
	
	private List<Assem> allocateMultipleFuncs(List<FuncDefnAssem> funcs) {
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
