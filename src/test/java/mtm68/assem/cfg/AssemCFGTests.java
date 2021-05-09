package mtm68.assem.cfg;

import static mtm68.assem.AssemTestUtils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import mtm68.assem.Assem;
import mtm68.assem.CmpAssem;
import mtm68.assem.JumpAssem;
import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.LabelAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.assem.op.AddAssem;
import mtm68.assem.operand.Loc;
import mtm68.util.ArrayUtils;

public class AssemCFGTests {
	
	private static Function<AssemData<String>, String> printer = o -> o.getAssem().toString();
	
	@Test
	void testAssemConstructionSimple() throws IOException {
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("t1"), reg("t2")),
				mov(reg("t3"), reg("t2")),
				mov(reg("t2"), reg("t4")),
				mov(reg("t8"), reg("t5")),
				mov(reg("t9"), reg("t10"))
			);
		
		AssemCFGBuilder<String> builder = new AssemCFGBuilder<>();
		Graph<AssemData<String>> graph = builder.buildAssemCFG(assems, () -> "wow");
		
		showOutput(graph);
	}

	@Test
	void testAssemConstructionWithJump() throws IOException {
		List<Assem> assems = ArrayUtils.elems(
				new LabelAssem("f"),
				mov(reg("t1"), reg("t2")),
				mov(reg("t3"), reg("t2")),
				new AddAssem(reg("t2"), reg("t4")),
				new CmpAssem(reg("t1"), reg("t2")),
				new JumpAssem(JumpType.JE, new Loc("f")),
				mov(reg("t8"), reg("t5")),
				mov(reg("t9"), reg("t10")),
				new RetAssem()
			);
		
		AssemCFGBuilder<String> builder = new AssemCFGBuilder<>();
		Graph<AssemData<String>> graph = builder.buildAssemCFG(assems, () -> "wow");
		
		showOutput(graph);
	}

	@Test
	void testAssemConstructionWithUnconditionalJump() throws IOException {
		List<Assem> assems = ArrayUtils.elems(
				new LabelAssem("f"),
				mov(reg("t1"), reg("t2")),
				jmp("f"),
				mov(reg("t3"), reg("t2")),
				new RetAssem()
			);
		
		AssemCFGBuilder<String> builder = new AssemCFGBuilder<>();
		Graph<AssemData<String>> graph = builder.buildAssemCFG(assems, () -> "wow");
		
		showOutput(graph);
	}
	
	@Test
	void testLabelInBetween() throws IOException {
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("t1"), reg("t2")),
				new LabelAssem("f"),
				mov(reg("t3"), reg("t2"))
			);
		
		AssemCFGBuilder<String> builder = new AssemCFGBuilder<>();
		Graph<AssemData<String>> graph = builder.buildAssemCFG(assems, () -> "wow");
		
		showOutput(graph);
	}

	@Test
	void testAfterRet() throws IOException {
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("t1"), reg("t2")),
				new RetAssem(),
				mov(reg("t3"), reg("t2")),
				mov(reg("t4"), reg("t2"))
			);
		
		AssemCFGBuilder<String> builder = new AssemCFGBuilder<>();
		Graph<AssemData<String>> graph = builder.buildAssemCFG(assems, () -> "wow");
		
		showOutput(graph);
	}
	
	private void showOutput(Graph<AssemData<String>> graph) throws IOException {
		graph.show(new PrintWriter(System.out), "CFG", true, printer);
	}

}
