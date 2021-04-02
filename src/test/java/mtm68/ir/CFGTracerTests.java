package mtm68.ir;
import static mtm68.ir.IRTestUtils.*;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.util.ArrayUtils.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.ir.cfg.CFGTracer;

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
		
		CFGBuilder builder = new CFGBuilder();
		
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
	
	private void printNodes(CFGBuilder builder) {
		System.out.println("Nodes:");
		System.out.println("==================");
		builder.getNodes().forEach(System.out::println);
		System.out.println();
	}
	
}
