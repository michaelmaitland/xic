package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.CFGBuilder;

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
		
		CFGBuilder builder = new CFGBuilder();
		
		stmts.forEach(builder::visitStatement);
		printNodes(builder);
	}
	
	private void printNodes(CFGBuilder builder) {
		builder.getNodes().forEach(System.out::println);
	}
}
