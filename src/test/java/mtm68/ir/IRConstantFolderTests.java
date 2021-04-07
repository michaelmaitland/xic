package mtm68.ir;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;

public class IRConstantFolderTests {

	@Test
	void testMultiLevelArithFold() {
		// (1 - 1) + 2
		IRBinOp op = new IRBinOp(ADD, 
						new IRBinOp (SUB,
								val(1L),
								val(1L)), 
						val(2L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(2, folded.value());
	}
	
	@Test
	void testMultiLevelDivByZeroFold() {
		// ((1 - 1) + 2) / (1 + (-1))
		IRBinOp op = new IRBinOp(DIV, 
						new IRBinOp(ADD, 
								new IRBinOp (SUB,
										val(1L),
										val(1L)), 
								val(2L)), 
						new IRBinOp(ADD,
								val(1L),
								val(-1L)));
		IRBinOp folded = assertInstanceOfAndReturn(IRBinOp.class, foldAndTestFolded(op));
		
		IRConst numerator = assertInstanceOfAndReturn(IRConst.class, folded.left());
		IRConst denom = assertInstanceOfAndReturn(IRConst.class, folded.right());

		assertEquals(2, numerator.value());
		assertEquals(0, denom.value());
	}
	
	@Test
	void testMultiLevelLogicFold() {
		// (1 XOR 0) && (0 || (1 == 3))
		IRBinOp op = new IRBinOp(AND, 
						new IRBinOp (XOR,
								val(1L),
								val(0L)), 
						new IRBinOp (OR,
								val(0L),
								new IRBinOp(EQ,
										val(3L),
										val(3L))));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testAddFold() {
		IRBinOp op = new IRBinOp(ADD, val(1L), val(2L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(3, folded.value());
	}
	
	@Test
	void testAndFold() {
		IRBinOp op = new IRBinOp(AND, val(1L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testARShiftFold() {
		IRBinOp op = new IRBinOp(ARSHIFT, val(-8L), val(2L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(-2, folded.value());
	}
	
	@Test
	void testDivFold() {
		IRBinOp op = new IRBinOp(DIV, val(2L), val(2L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
		
		IRBinOp divByZero = new IRBinOp(DIV, val(2L), val(0L));
		IRBinOp folded2 = assertInstanceOfAndReturn(IRBinOp.class, foldAndTestFolded(divByZero));
		
		assertEquals(divByZero, folded2);
	}
	
	@Test
	void testEqFold() {
		IRBinOp op = new IRBinOp(EQ, val(1L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testGeqFold() {
		IRBinOp op = new IRBinOp(GEQ, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testGtFold() {
		IRBinOp op = new IRBinOp(GT, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testHighMulFold() {
		IRBinOp op = new IRBinOp(HMUL, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(0, folded.value());
	}
	
	@Test
	void testLeqFold() {
		IRBinOp op = new IRBinOp(LEQ, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(0, folded.value());
	}
	
	@Test
	void testLShiftFold() {
		IRBinOp op = new IRBinOp(LSHIFT, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(8, folded.value());
	}
	
	@Test
	void testLTFold() {
		IRBinOp op = new IRBinOp(LT, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(0, folded.value());
	}
	
	@Test
	void testModFold() {
		IRBinOp op = new IRBinOp(MOD, val(5L), val(3L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(2, folded.value());
		
		IRBinOp modByZero = new IRBinOp(MOD, val(2L), val(0L));
		IRBinOp folded2 = assertInstanceOfAndReturn(IRBinOp.class, foldAndTestFolded(modByZero));
		
		assertEquals(modByZero, folded2);
	}
	
	@Test
	void testMulFold() {
		IRBinOp op = new IRBinOp(MUL, val(4L), val(3L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(12, folded.value());
	}
	
	@Test
	void testNEQFold() {
		IRBinOp op = new IRBinOp(NEQ, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testORFold() {
		IRBinOp op = new IRBinOp(OR, val(0L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(1, folded.value());
	}
	
	@Test
	void testRShiftFold() {
		IRBinOp op = new IRBinOp(RSHIFT, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(2, folded.value());
	}
	
	@Test
	void testSubFold() {
		IRBinOp op = new IRBinOp(SUB, val(4L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(3, folded.value());
	}
	
	@Test
	void testXORFold() {
		IRBinOp op = new IRBinOp(XOR, val(1L), val(1L));
		IRConst folded = assertInstanceOfAndReturn(IRConst.class, foldAndTestFolded(op));
		
		assertEquals(0, folded.value());
	}
	
	private IRNode foldAndTestFolded(IRNode node) {
		IRConstantFolder folder = new IRConstantFolder(new IRNodeFactory_c());
		IRNode folded = folder.visit(node);
		
		CheckConstFoldedIRVisitor checker = new CheckConstFoldedIRVisitor();
		assertTrue("IRNode is not properly folded" , checker.visit(folded));
		
		return folded;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T assertInstanceOfAndReturn(Class<T> clazz, Object obj) {
		assertTrue(obj.getClass() + " is not an instanceof " + clazz , clazz.isAssignableFrom(obj.getClass()));
		return (T) obj;
	}
	
	private IRConst val(long value) {
		return new IRConst(value);
	}
}
