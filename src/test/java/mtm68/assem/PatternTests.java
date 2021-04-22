package mtm68.assem;

import static mtm68.ir.IRTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
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
						Patterns.var(), 
						Patterns.anyConstant()));
		
		IRMem mem = mem(op(OpType.ADD, constant(12L), temp("t")));
		
		assertTrue(pattern.matches(mem));
		
		System.out.println(pattern.getPatternMatches());
	}

	@Test
	void testMoveMemPattern() {
		// move [t1 + i * t2], c 
		Pattern pattern = Patterns.move(
				Patterns.mem(
						Patterns.op(
								OpType.ADD,
								Patterns.var(),
								Patterns.op(OpType.MUL, 
										Patterns.index(), 
										Patterns.var())
								)),
				Patterns.anyConstant()
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
		
		System.out.println(pattern.getPatternMatches());
	}

}
