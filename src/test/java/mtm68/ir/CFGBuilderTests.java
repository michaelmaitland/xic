package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.ir.cfg.CFGBuilder.CFGMode;
import mtm68.ir.cfg.CFGBuilder.CFGNode;

public class CFGBuilderTests {
	
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
		
		CFGBuilder builder = new CFGBuilder(CFGMode.STMT);
		
		stmts.forEach(builder::visitStatement);
		printNodes(builder);
	}
	
	@Test
	void interestingJumpsTest() {
		List<IRStmt> stmts = elems(
				label("l0"),
				move("c", "b"),
				jump("l2"),
				label("l2"),
				move("x", "y"),
				cjump("l3", null),
				move("y", "x"),
				label("l3"),
				move("z", "x"),
				cjump("l2", "l3")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.STMT);
		
		stmts.forEach(builder::visitStatement);
		printNodes(builder);
	}

	@Test
	void testRetNoOutgoingEdges() {
		List<IRStmt> stmts = elems(
				label("l0"),
				jump("l2"),
				label("l1"),
				ret(),
				label("l2")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);
		
		List<CFGNode> nodes = builder.getNodes();
		assertEquals(3, nodes.size());
		
		// Node 1 has no outgoing edges because it ends with a return stmt
		assertNoOutgoing(nodes, 1);
	}

	@Test
	void testLabelAfterReturn() {
		List<IRStmt> stmts = elems(
				ret(),
				label("l0")
			);
		
		CFGBuilder builder = new CFGBuilder(CFGMode.BB);
		stmts.forEach(builder::visitStatement);
		
		List<CFGNode> nodes = builder.getNodes();
		assertEquals(2, nodes.size());
	}
	
	private void assertNoOutgoing(List<CFGNode> nodes, int nodeIdx) {
		assertEquals(0, nodes.get(nodeIdx).getOut().size());
	}
	
	private void printNodes(CFGBuilder builder) {
		builder.getNodes().forEach(System.out::println);
	}
}
