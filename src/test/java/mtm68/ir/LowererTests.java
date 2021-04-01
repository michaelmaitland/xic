package mtm68.ir;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.ArrayUtils.empty;
import static mtm68.util.ArrayUtils.singleton;

public class LowererTests {

	//-------------------------------------------------------------------------------- 
	// Const
	//-------------------------------------------------------------------------------- 

	@Test
	void lowerConst() {
		IRConst cons = new IRConst(1L);
		IRConst lowered = assertInstanceOfAndReturn(IRConst.class, lowerNodeAndCheckCanonical(cons));
		
		assertTrue(lowered.getSideEffects().isEmpty());
	}
	
	//-------------------------------------------------------------------------------- 
	// Name
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerName() {
		IRName name = new IRName("test");
		IRName lowered = assertInstanceOfAndReturn(IRName.class, lowerNodeAndCheckCanonical(name));
		
		assertTrue(lowered.getSideEffects().isEmpty());

	}
	
	//-------------------------------------------------------------------------------- 
	// Temp
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerTemp() {
		IRTemp temp = new IRTemp("t1");
		IRTemp lowered = assertInstanceOfAndReturn(IRTemp.class, lowerNodeAndCheckCanonical(temp));

		assertTrue(lowered.getSideEffects().isEmpty());
	}
	
	//-------------------------------------------------------------------------------- 
	// Mem
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerMemNoSideEffects() {
		IRMem mem = new IRMem(new IRConst(1L));
		IRMem lowered = assertInstanceOfAndReturn(IRMem.class, lowerNodeAndCheckCanonical(mem));
		
		assertTrue(lowered.getSideEffects().isEmpty());
	}
	
	@Test
	void lowerMemSideEffects() {
		IRMem mem = new IRMem(exprWithTwoSE());
		IRMem lowered = assertInstanceOfAndReturn(IRMem.class, lowerNodeAndCheckCanonical(mem));
				
		assertEquals(2, lowered.getSideEffects().size());
		assertTrue(lowered.expr() instanceof IRConst);	}
	
	//-------------------------------------------------------------------------------- 
	// Jump
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerJumpNoSideEffects() {
		IRJump jump = new IRJump(new IRConst(1L));
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(jump));
		
		assertEquals(1, lowered.stmts().size());
	}
	
	@Test
	void lowerJumpSideEffects() {
		IRJump jump = new IRJump(exprWithTwoSE());
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(jump));
		
		IRJump loweredJump = (IRJump) lowered.stmts().get(2);
		
		assertEquals(3, lowered.stmts().size());
		assertTrue(loweredJump.target() instanceof IRConst);
	}
	
	//-------------------------------------------------------------------------------- 
	// CJump
	//-------------------------------------------------------------------------------- 

	@Test
	void lowerCJumpNoSideEffects() {
		IRCJump jump = new IRCJump(new IRConst(1L), "lf");
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(jump));
		
		assertEquals(1, lowered.stmts().size());
	}
	
	@Test
	void lowerCJumpSideEffects() {
		IRCJump jump = new IRCJump(exprWithTwoSE(), "lt");
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(jump));
		
		IRCJump loweredJump = (IRCJump) lowered.stmts().get(2);
		
		assertEquals(3, lowered.stmts().size());
		assertTrue(loweredJump.cond() instanceof IRConst);
	}
	
	//-------------------------------------------------------------------------------- 
	// ESeq
	//-------------------------------------------------------------------------------- 

	@Test
	void lowerESeqNoSideEffects() {
		IRESeq eseq = new IRESeq(genericSeq(), genericConst());
		IRConst lowered = assertInstanceOfAndReturn(IRConst.class, lowerNodeAndCheckCanonical(eseq));
		
		assertEquals(genericSeq().stmts().size(), lowered.getSideEffects().size());
	}
	
	@Test
	void lowerESeqSideEffects() {
		IRESeq eseq = new IRESeq(genericSeq(), exprWithTwoSE());
		IRConst lowered = assertInstanceOfAndReturn(IRConst.class, lowerNodeAndCheckCanonical(eseq));
		
		assertEquals(genericSeq().stmts().size() + 2, lowered.getSideEffects().size());
	}
	
	//-------------------------------------------------------------------------------- 
	// Call / CallStmt
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerFuncNoArgs() {
		IRCall call = new IRCall(new IRName("f"), empty());
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(call));
		
		assertEquals(1, lowered.stmts().size());
	}
	
	@Test
	void lowerFuncArgsNoSideEffects() {
		IRCall call = new IRCall(new IRName("f"), elems(new IRConst(1L), new IRConst(2L)));
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(call));
		
		assertEquals(3, lowered.stmts().size());
		
		IRMove arg0 = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(0));
		IRMove arg1 = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(1));
		IRCallStmt loweredCall = assertInstanceOfAndReturn(IRCallStmt.class, lowered.stmts().get(2));
		
		IRTemp savedArg0Temp = assertInstanceOfAndReturn(IRTemp.class, arg0.target());
		assertEquals("_ARG0", savedArg0Temp.name());
		IRConst arg0Const = assertInstanceOfAndReturn(IRConst.class, arg0.source());
		assertEquals(1L, arg0Const.constant());
		
		
		IRTemp savedArg1Temp = assertInstanceOfAndReturn(IRTemp.class, arg1.target());
		assertEquals("_ARG1", savedArg1Temp.name());
		IRConst arg1Const = assertInstanceOfAndReturn(IRConst.class, arg1.source());
		assertEquals(2L, arg1Const.constant());
		
		assertEquals(2, loweredCall.args().size());
		IRTemp arg0Temp = assertInstanceOfAndReturn(IRTemp.class, loweredCall.args().get(0));
		IRTemp arg1Temp = assertInstanceOfAndReturn(IRTemp.class, loweredCall.args().get(1));
		
		assertEquals(savedArg0Temp.name(), arg0Temp.name());
		assertEquals(savedArg1Temp.name(), arg1Temp.name());
	}
	

	@Test
	void lowerFuncArgsSideEffects() {
		IRCall call = new IRCall(new IRName("f"), elems(exprWithTwoSE()));
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(call));
		
		// 1 for call, 1 for arg prep, 2 for side effects
		assertEquals(4, lowered.stmts().size());
		
	    assertInstanceOfAndReturn(IRJump.class, lowered.stmts().get(0));
		assertInstanceOfAndReturn(IRLabel.class, lowered.stmts().get(1));
		IRMove arg0 = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(2));
		IRCallStmt loweredCall = assertInstanceOfAndReturn(IRCallStmt.class, lowered.stmts().get(3));
		
		IRTemp savedArg0Temp = assertInstanceOfAndReturn(IRTemp.class, arg0.target());
		assertEquals("_ARG0", savedArg0Temp.name());
		IRConst arg0Const = assertInstanceOfAndReturn(IRConst.class, arg0.source());
		assertEquals(1L, arg0Const.constant());
		
		assertEquals(1, loweredCall.args().size());
		IRTemp arg0Temp = assertInstanceOfAndReturn(IRTemp.class, loweredCall.args().get(0));
		
		assertEquals(savedArg0Temp.name(), arg0Temp.name());
	}
	
	@Test
	void lowerFuncMultiArgsSideEffects() {
		IRCall call = new IRCall(new IRName("f"), elems(exprWithTwoSE(), exprWithTwoSE()));
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(call));
		
		// 1 for call, 2 for arg prep, 4 for side effects
		assertEquals(7, lowered.stmts().size());
		
		assertInstanceOfAndReturn(IRJump.class, lowered.stmts().get(0));
		assertInstanceOfAndReturn(IRLabel.class, lowered.stmts().get(1));
		IRMove arg0 = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(2));
		assertInstanceOfAndReturn(IRJump.class, lowered.stmts().get(3));
		assertInstanceOfAndReturn(IRLabel.class, lowered.stmts().get(4));
		IRMove arg1 = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(5));
		IRCallStmt loweredCall = assertInstanceOfAndReturn(IRCallStmt.class, lowered.stmts().get(6));
		
		IRTemp  savedArg0Temp = assertInstanceOfAndReturn(IRTemp.class, arg0.target());
		assertEquals("_ARG0", savedArg0Temp.name());
		IRConst arg0Const = assertInstanceOfAndReturn(IRConst.class, arg0.source());
		assertEquals(1L, arg0Const.constant());
		
		IRTemp savedArg1Temp = assertInstanceOfAndReturn(IRTemp.class, arg1.target());
		assertEquals("_ARG1", savedArg1Temp.name());
		IRConst arg1Const = assertInstanceOfAndReturn(IRConst.class, arg1.source());
		assertEquals(1L, arg1Const.constant());
		
		assertEquals(2, loweredCall.args().size());
		IRTemp arg0Temp = assertInstanceOfAndReturn(IRTemp.class, loweredCall.args().get(0));
		IRTemp arg1Temp = assertInstanceOfAndReturn(IRTemp.class, loweredCall.args().get(1));
		
		assertEquals(savedArg0Temp.name(), arg0Temp.name());
		assertEquals(savedArg1Temp.name(), arg1Temp.name());
	}
	
	//-------------------------------------------------------------------------------- 
	// BinOp
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerBinOpNoSideEffects() {
		IRBinOp add = new IRBinOp(IRBinOp.OpType.ADD, genericConst(), genericConst());
		IRBinOp lowered = assertInstanceOfAndReturn(IRBinOp.class, lowerNodeAndCheckCanonical(add));

		assertEquals(0, lowered.getSideEffects().size());
	}
	
	@Test
	void lowerBinOpSideEffectsNoCommute() {
		// ADD (ESEQ(LABEL(l); x), ESEQ(MOVE(x, y), CONST(1)))
		IRExpr left = new IRESeq(new IRLabel("l"), new IRMem(new IRTemp("x")));
		IRExpr right = new IRESeq(new IRMove(new IRTemp("x") , new IRTemp("y")), new IRConst(1L));
		IRBinOp add = new IRBinOp(IRBinOp.OpType.ADD, left, right);
		IRBinOp lowered = assertInstanceOfAndReturn(IRBinOp.class, lowerNodeAndCheckCanonical(add));

		// 1 for label, 1 for left move into temp, 1 for Move
		assertEquals(3, lowered.getSideEffects().size());	
		assertInstanceOfAndReturn(IRLabel.class, lowered.getSideEffects().get(0));
		IRMove leftSave = assertInstanceOfAndReturn(IRMove.class, lowered.getSideEffects().get(1));
		assertInstanceOfAndReturn(IRMove.class, lowered.getSideEffects().get(2));
		
		IRTemp savedLeftTemp = assertInstanceOfAndReturn(IRTemp.class, leftSave.target());
		assertTrue(leftSave.source() instanceof IRMem);
		
		IRTemp leftTemp = assertInstanceOfAndReturn(IRTemp.class, lowered.left());
		assertEquals(savedLeftTemp.name(), leftTemp.name());
	}
	
	@Test
	void lowerBinOpSideEffectsCommute() {
		fail("Not implemented");
	}
	
	//-------------------------------------------------------------------------------- 
	// Move
	//-------------------------------------------------------------------------------- 
	
	@Test
	void lowerMoveNoSideEffects() {
		IRMove move = new IRMove(new IRTemp("x"), genericConst());
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(move));

		assertEquals(1, lowered.stmts().size());
	}
	
	@Test
	void lowerMoveSideEffectsNoCommute() {
		IRMem target = new IRMem(new IRTemp("x"));
		IRESeq src = new IRESeq(new IRMove(new IRTemp("x"), new IRTemp("y")), genericConst());
		
		IRMove move = new IRMove(target, src);
		IRSeq lowered = assertInstanceOfAndReturn(IRSeq.class, lowerNodeAndCheckCanonical(move));
		
		assertEquals(3, lowered.stmts().size());
		
		IRMove targetSave = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(0));
		assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(1)); //Side effect move
		IRMove newMove = assertInstanceOfAndReturn(IRMove.class, lowered.stmts().get(2));

		IRTemp targetSaveTemp = assertInstanceOfAndReturn(IRTemp.class, targetSave.target());
		IRMem newMoveTarget = assertInstanceOfAndReturn(IRMem.class, newMove.target());
		IRTemp newMoveTargetTemp = assertInstanceOfAndReturn(IRTemp.class, newMoveTarget.expr());
		
		assertEquals(targetSaveTemp.name(), newMoveTargetTemp.name());
	}
	
	@Test
	void lowerMoveSideEffectsCommute() {
		fail("Not implemented");
	}
	
	//-------------------------------------------------------------------------------- 
	// Return
	//-------------------------------------------------------------------------------- 
	
	//-------------------------------------------------------------------------------- 
	// Seq
	//-------------------------------------------------------------------------------- 
	
	//-------------------------------------------------------------------------------- 
	// Exp
	//-------------------------------------------------------------------------------- 
	
	//-------------------------------------------------------------------------------- 
	// Helper Methods
	//-------------------------------------------------------------------------------- 

	private IRExpr exprWithTwoSE() {
		return new IRESeq(genericSeq(), genericConst());
	}
	
	private IRSeq genericSeq() {
		IRSeq seq = new IRSeq(new IRJump(new IRConst(1L)), new IRLabel("f"));
		return seq;
	}
	
	private IRExpr genericConst() {
		return new IRConst(1L);
	}
	
	@SuppressWarnings("unchecked")
	private <N extends IRNode> N lowerNodeAndCheckCanonical(N node) {
		Lowerer lowerer = new Lowerer(new IRNodeFactory_c());
		N lowered = (N) lowerer.visit(node);
		
		CheckCanonicalIRVisitor v = new CheckCanonicalIRVisitor();
		v.visit(lowered);
		
		assertNull(v.noncanonical());
		return lowered;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T assertInstanceOfAndReturn(Class<T> clazz, Object obj) {
		assertTrue(obj.getClass() + " is not an instanceof " + clazz , clazz.isAssignableFrom(obj.getClass()));
		return (T) obj;
	}
}
