package mtm68.assem;

import static mtm68.ir.IRTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import mtm68.assem.pattern.Pattern;
import mtm68.assem.pattern.Patterns;

public class PatternTests {
	
	@Test
	void testMemAddPattern() {
		Pattern pattern = Patterns.mem(
				Patterns.op(
						OpType.ADD, 
						Patterns.var("t"), 
						Patterns.anyConstant("c")));
		
		IRMem mem = mem(op(OpType.ADD, constant(12L), temp("t")));
		
		assertTrue(pattern.matches(mem));
		
		Map<String, IRExpr> matches = new HashMap<>();
		pattern.addMatchedExprs(matches);

		System.out.println(matches);
	}

	@Test
	void testMoveMemPattern() {
		// move [t1 + i * t2], c 
		Pattern pattern = Patterns.move(
				Patterns.mem(
						Patterns.op(
								OpType.ADD,
								Patterns.var("t1"),
								Patterns.op(OpType.MUL, 
										Patterns.index("i"), 
										Patterns.var("t2"))
								)),
				Patterns.anyConstant("c")
				);

		IRMove move = move(
				mem(op(OpType.ADD,
						op(OpType.MUL,
								mem(op(OpType.ADD, constant(12L), temp("t"))),
								constant(4L)
								),
						mem(op(OpType.ADD,
								temp("t"),
								constant(8L)))
						)),
				constant(7L)
			);
		
		assertTrue(pattern.matches(move));
		
		Map<String, IRExpr> matches = new HashMap<>();
		pattern.addMatchedExprs(matches);

		System.out.println(matches);
	}

}
