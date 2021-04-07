package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.*;
import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;
import static mtm68.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.visit.CFGVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;

public class CFGVisitorTests {

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

		IRSeq seq = new IRSeq(stmts);

		System.out.println("Before:");
		System.out.println("==================");
		printStatements(seq);
		System.out.println();
		
		CFGVisitor v = new CFGVisitor(new IRNodeFactory_c());
		IRNode result = v.visit(seq);
		
		CheckCanonicalIRVisitor checkCanVisitor = new CheckCanonicalIRVisitor();
		boolean isCanonical = checkCanVisitor.visit(result);
		
		assertTrue(isCanonical);

		IRSeq newSeq = assertInstanceOfAndReturn(IRSeq.class, result);

		System.out.println("After:");
		System.out.println("==================");
		printStatements(newSeq);
		System.out.println();
	}
	
	private void printStatements(IRSeq seq) {
		seq.stmts().forEach(System.out::print);
	}
		
}
