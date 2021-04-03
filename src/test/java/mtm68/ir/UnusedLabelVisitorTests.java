package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;


public class UnusedLabelVisitorTests {
	
	@Test
	void testRemoveUnusedLabelClassExample() {
		
		List<IRStmt> stmts = elems(
				label("l0"),
				cjump(op(OpType.XOR, constant(0L), constant(1L)), "l3", null),
				label("l2"),
				move("x", op(ADD, temp("y"), temp("z"))),
				label("l1"),
				move("x", "y"),
				jump("l2"),
				label("l3"),
				call("f"),
				ret()
			);
		
		IRSeq seq = new IRSeq(stmts);
		
		UnusedLabelVisitor v = new UnusedLabelVisitor();
		IRNode result = v.visit(seq);
		
		printStatements((IRSeq)result);
	}

	private void printStatements(IRSeq seq) {
		seq.stmts().forEach(System.out::print);
	}
}
