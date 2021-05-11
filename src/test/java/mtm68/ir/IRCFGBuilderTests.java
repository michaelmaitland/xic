package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.assem.cfg.Graph;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.ir.cfg.CFGBuilder.CFGMode;
import mtm68.ir.cfg.CFGBuilder.CFGNode;
import mtm68.ir.cfg.IRCFGBuilder;
import mtm68.ir.cfg.IRData;

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

	
	private void showOutput(Graph<IRData<String>> graph) throws IOException {
		graph.show(new PrintWriter(System.out), "CFG", true, printer);
	}
}
