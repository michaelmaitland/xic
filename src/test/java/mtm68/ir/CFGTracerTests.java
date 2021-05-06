package mtm68.ir;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;
import static mtm68.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCJump;
import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRJump;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.ir.cfg.CFGTracer;
import mtm68.ir.cfg.CFGBuilder.CFGMode;

public class CFGTracerTests {

	@Test
	void exampleFromClass() {
		List<IRStmt> stmts = elems(
				label("l0"),
				cjump("l2", "l3"),
				label("l1"),
				move("x", "y"),
				label("l2"),
				move("x", op(ADD, temp("y"), temp("z"))),
				jump("l1"),
				label("l3"),
				call("f"),
				ret()
			);
		
		System.out.println("Before:");
		System.out.println("==================");
		stmts.forEach(System.out::print);
		System.out.println();
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		
		stmts.forEach(builder::visitStatement);

		printNodes(builder);
		
		System.out.println("Traces:");
		System.out.println("==================");

		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> reordered = tracer.performReordering();

		System.out.println();
		
		System.out.println("After:");
		System.out.println("==================");

		reordered.forEach(System.out::print);

		System.out.println();
	}

	@Test
	void exampleFromClassIsCanonical() {
		List<IRStmt> stmts = elems(
				label("l0"),
				cjump("l2", "l3"),
				label("l1"),
				move("x", "y"),
				label("l2"),
				move("x", op(ADD, temp("y"), temp("z"))),
				jump("l1"),
				label("l3"),
				call("f"),
				ret()
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);

		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> reordered = tracer.performReordering();
		
		IRSeq seq = new IRSeq(reordered);
		assertCanonical(seq);
	}

	@Test
	void testCJumpFlipCondition() {
		List<IRStmt> stmts = elems(
				cjump("l0", "l1"),
				label("l0"),
				label("l1")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);

		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> reordered = tracer.performReordering();
		
		IRSeq seq = new IRSeq(reordered);
		assertCanonical(seq);
		
		IRCJump jump = assertInstanceOfAndReturn(IRCJump.class, reordered.get(0));
		assertEquals("l1", jump.trueLabel());
		assertNull(jump.falseLabel());
		
		IRBinOp op = assertInstanceOfAndReturn(IRBinOp.class, jump.cond());
		assertEquals(OpType.XOR, op.opType());
		
		assertEquals(1L, ((IRConst)op.right()).value());
	}

	@Test
	void testCJumpFallthrough() {
		List<IRStmt> stmts = elems(
				label("l0"),
				label("l1"),
				cjump("l0", "l1")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);

		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> reordered = tracer.performReordering();
		
		IRSeq seq = new IRSeq(reordered);
		assertCanonical(seq);
		
		IRCJump cjump = assertInstanceOfAndReturn(IRCJump.class, reordered.get(2));
		assertEquals("l0", cjump.trueLabel());
		assertNull(cjump.falseLabel());

		IRJump jump = assertInstanceOfAndReturn(IRJump.class, reordered.get(3));
		assertEquals("l1", ((IRName)jump.target()).name());
	}

	@Test
	void testUnnecessaryJumpRemove() {
		List<IRStmt> stmts = elems(
				label("l0"),
				jump("l2"),
				label("l1"),
				ret(),
				label("l2")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);

		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> reordered = tracer.performReordering();
		
		IRSeq seq = new IRSeq(reordered);
		assertCanonical(seq);
		
		assertNoJumps(reordered);
		assertEquals(4, reordered.size());
	}

	@Test
	void testAddJumpNoFallthrough() {
		List<IRStmt> stmts = elems(
				label("l0"),
				jump("l2"),
				label("l1"),
				label("l2")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);

		CFGTracer tracer = new CFGTracer(builder.getNodes(), stmts);
		List<IRStmt> reordered = tracer.performReordering();
		
		IRSeq seq = new IRSeq(reordered);
		assertCanonical(seq);
		
		assertEquals(4, reordered.size());

		IRJump jump = assertInstanceOfAndReturn(IRJump.class, reordered.get(3));
		assertEquals("l2", ((IRName)jump.target()).name());
	}
	
	private void assertNoJumps(List<IRStmt> result) {
		boolean anyJumps = result.stream()
			.anyMatch(s -> s instanceof IRJump);
		
		assertFalse(anyJumps, "Expected no jumps but got some");
	}
	
	private void printNodes(CFGBuilder builder) {
		System.out.println("Nodes:");
		System.out.println("==================");
		builder.getNodes().forEach(System.out::println);
		System.out.println();
	}
	
}
