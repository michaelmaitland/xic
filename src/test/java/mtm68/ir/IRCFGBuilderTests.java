package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.ADD;
import static mtm68.ir.IRTestUtils.call;
import static mtm68.ir.IRTestUtils.cjump;
import static mtm68.ir.IRTestUtils.jump;
import static mtm68.ir.IRTestUtils.label;
import static mtm68.ir.IRTestUtils.move;
import static mtm68.ir.IRTestUtils.op;
import static mtm68.ir.IRTestUtils.ret;
import static mtm68.ir.IRTestUtils.temp;
import static mtm68.ir.IRTestUtils.constant;
import static mtm68.util.ArrayUtils.elems;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.assem.cfg.Graph;
import mtm68.ir.cfg.IRCFGBuilder;
import mtm68.ir.cfg.IRCFGBuilder.IRData;

public class IRCFGBuilderTests {
	
	private static Function<IRData<String>, String> printer = o -> o.getIR().toString();
	
	@Test
	void exampleFromClass() throws IOException {
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
		
		IRCFGBuilder<String> builder = new IRCFGBuilder<>();
		Graph<IRData<String>> graph = builder.buildIRCFG(stmts, () -> "wow");
		
		showOutput(graph);
	}
	
	@Test
	void interestingJumpsTest() throws IOException {
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
		
		IRCFGBuilder<String> builder = new IRCFGBuilder<>();
		Graph<IRData<String>> graph = builder.buildIRCFG(stmts, () -> "wow");
		
		showOutput(graph);
	}
	
	@Test
	void twoXGetsE() throws IOException {
		List<IRStmt> stmts = elems(
				move(temp("t1"), constant(1)),
				move(temp("t2"), constant(2))
			);
		
		IRCFGBuilder<String> builder = new IRCFGBuilder<>();
		Graph<IRData<String>> graph = builder.buildIRCFG(stmts, () -> "wow");
		
		showOutput(graph);
	}
	
	@Test
	void jumpsAndLabelsTest() throws IOException {
		List<IRStmt> stmts = elems(
				label("l0"),
				label("l1"),
				move("c", "b"),
				move("x", "y"),
				cjump("l0", null),
				cjump("l1", null),
				label("l3"),
				move("x", "z"),
				move("a", "b"),
				label("l4"),
				cjump("l3", "l5"),
				label("l5"),
				jump("l0")
			);
		
		IRCFGBuilder<String> builder = new IRCFGBuilder<>();
		Graph<IRData<String>> graph = builder.buildIRCFG(stmts, () -> "wow");
		
		showOutput(graph);
	}
	
	private void showOutput(Graph<IRData<String>> graph) throws IOException {
		graph.show(new PrintWriter(System.out), "CFG", true, printer);
	}
}
