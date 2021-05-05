package mtm68.assem.cfg;

import static mtm68.assem.AssemTestUtils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.assem.Assem;
import mtm68.assem.cfg.AssemCFGBuilder.AssemData;
import mtm68.util.ArrayUtils;

public class AssemCFGTests {
	
	@Test
	void testAssemConstruction() throws IOException {
		List<Assem> assems = ArrayUtils.elems(
				mov(reg("t1"), reg("t2")),
				mov(reg("t3"), reg("t2")),
				mov(reg("t2"), reg("t4")),
				mov(reg("t8"), reg("t5")),
				mov(reg("t9"), reg("t10"))
			);
		
		AssemCFGBuilder<String> builder = new AssemCFGBuilder<>();
		Graph<AssemData<String>> graph = builder.buildAssemCFG(assems, () -> "wow");
		
		graph.show(new PrintWriter(System.out), "CFG");
		
	}

}
