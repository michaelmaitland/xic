package mtm68.assem;

public class PatternTests {
	
//	@Test
//	void testMemAddPattern() {
//		Pattern pattern = Patterns.mem(
//				Patterns.op(
//						OpType.ADD, 
//						Patterns.var("t"), 
//						Patterns.anyConstant("c")));
//		
//		IRMem mem = mem(op(OpType.ADD, constant(12L), temp("t")));
//		
//		assertTrue(pattern.matches(mem));
//		
//		Map<String, IRExpr> matches = new HashMap<>();
//		pattern.addMatchedExprs(matches);
//
//		System.out.println(matches);
//	}
//
//	@Test
//	void testMoveMemPattern() {
//		// move [t1 + i * t2], c 
//		Pattern pattern = Patterns.move(
//				Patterns.mem(
//						Patterns.op(
//								OpType.ADD,
//								Patterns.var("t1"),
//								Patterns.op(OpType.MUL, 
//										Patterns.index("i"), 
//										Patterns.var("t2"))
//								)),
//				Patterns.anyConstant("c")
//				);
//
//		IRMove move = move(
//				mem(op(OpType.ADD,
//						op(OpType.MUL,
//								mem(op(OpType.ADD, constant(12L), temp("t"))),
//								constant(4L)
//								),
//						mem(op(OpType.ADD,
//								temp("t"),
//								constant(8L)))
//						)),
//				constant(7L)
//			);
//		
//		assertTrue(pattern.matches(move));
//		
//		Map<String, IRExpr> matches = new HashMap<>();
//		pattern.addMatchedExprs(matches);
//
//		System.out.println(matches);
//	}

}
