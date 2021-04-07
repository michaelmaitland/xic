package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;
import static mtm68.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;


public class UnusedLabelVisitorTests {
	
	@Test
	void testRemoveUnusedLabelClassExample() {
		List<IRStmt> stmts = elems(
				label("l0"),
				cjump(op(OpType.XOR, constant(0L), constant(1L)), "l3", null),
				label("l2"),
				move("x", op(ADD, temp("y"), temp("z"))),
				label("l1"),
				move("x", "y"),
				jump("l2"),
				label("l3"),
				call("f"),
				ret()
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result, "l0", "l1");
	}

	@Test
	void testNoLabelsRemoved() {
		List<IRStmt> stmts = elems(
				cjump(op(OpType.XOR, constant(0L), constant(1L)), "l3", null),
				label("l2"),
				move("x", op(ADD, temp("y"), temp("z"))),
				move("x", "y"),
				jump("l2"),
				label("l3"),
				call("f"),
				ret()
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result);
	}

	@Test
	void testAllLabelsRemoved() {
		List<IRStmt> stmts = elems(
				label("l0"),
				label("l1"),
				label("l2"),
				label("l3")
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result, "l0", "l1", "l2", "l3");
		assertEquals(0, result.stmts().size());
	}

	@Test
	void testBothCJumpBranchesRetainLabels() {
		List<IRStmt> stmts = elems(
				label("l0"),
				label("l1"),
				cjump("l0", "l1"),
				label("l3")
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result, "l3");
		assertEquals(3, result.stmts().size());
	}

	@Test
	void testCJumpNullBranchOk() {
		List<IRStmt> stmts = elems(
				label("l0"),
				label("l1"),
				cjump(null, null),
				label("l3")
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result, "l0", "l1", "l3");
		assertEquals(1, result.stmts().size());
	}

	@Test
	void testCallSavesLabels() {
		List<IRStmt> stmts = elems(
				call("l0"),
				label("l0"),
				label("l1")
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result, "l1");
		assertEquals(2, result.stmts().size());
	}

	@Test
	void testJumpSavesLabels() {
		List<IRStmt> stmts = elems(
				jump("l0"),
				label("l0"),
				label("l1")
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode node = v.visit(seq);
		IRSeq result = assertInstanceOfAndReturn(IRSeq.class, node);
		
		assertRemovedLabels(seq, result, "l1");
		assertEquals(2, result.stmts().size());
	}

	private void assertRemovedLabels(IRSeq original, IRSeq result, String...labels) {
		Set<String> originalLabels = getLabels(original); 
		Set<String> resultLabels = getLabels(result); 
		
		Set<String> labelSet = new HashSet<>(Arrays.asList(labels));
		
		originalLabels.removeIf(resultLabels::contains);
		
		assertEquals(labelSet, originalLabels);
	}
	
	private Set<String> getLabels(IRSeq seq) {
		return seq.stmts().stream()
			.filter(s -> s instanceof IRLabel)
			.map(s -> (IRLabel)s)
			.map(l -> l.name())
			.collect(Collectors.toSet());
	}
}
