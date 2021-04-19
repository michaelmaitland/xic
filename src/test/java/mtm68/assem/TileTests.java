package mtm68.assem;

import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.ArrayUtils.*;
import static mtm68.util.TestUtils.*;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import mtm68.assem.op.LeaAssem;

public class TileTests {

	@Test
	void tileAdd() {
		IRNode plus = op(OpType.ADD, temp("t1"), temp("t2"));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(plus));
		
	}
	
	@Test
	void tileJump() {
		IRNode jump = jump("f");
		JumpAssem tiled = assertInstanceOfAndReturn(JumpAssem.class, tile(jump));
		
	}
	
	@Test
	void tileMem() {
		IRNode mem = mem(temp("t1"));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(mem));
		
	}
	
	@Test
	void tileMoveRegToReg() {
		IRNode move = move("t1", "t2");
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
	}
	
	@Test
	void tileMoveMemToReg() {
		IRNode move = move("t1", mem(temp("t2")));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
	}
	
	@Test
	void tileMoveRegToMem() {
		IRNode move = move(mem(temp("t2")), temp("t1"));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
	}
	
	@Test
	void tileMoveRegTtoMem2() {
		IRNode move = move(mem(op(OpType.ADD, temp("t1"), temp("t2"))), op(OpType.ADD, temp("t3"), temp("t4")));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
	}

	@Test
	void tileSeq() {
		IRSeq seq = new IRSeq(elems(
					move("t1", "t2"),
					move(mem(temp("t3")), op(OpType.ADD, temp("t4"), temp("t5")))
				));
		
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(seq));
	}
	
	@Test
	void tileCallExtraArgsAndRets() {
		IRCallStmt call = new IRCallStmt(new IRName("f"), 4, 
				temp("t1"),
				temp("t2"),
				temp("t3"),
				op(OpType.ADD, temp("t4"), temp("t4")),
				temp("t5"),
				temp("t6"),
				temp("t7"),
				temp("t8")
				);
		
		IRMove movRet2 = new IRMove(temp("t9"), temp("_RET2"));
		IRMove movRet3 = new IRMove(temp("t10"), temp("_RET3"));
		
		IRSeq seq = new IRSeq(call, movRet2, movRet3, ret());
		
		tile(seq);
	}
	
	@Test
	void tileCallExtraArgs() {
		IRCallStmt call = new IRCallStmt(new IRName("f"), 0, 
				temp("t1"),
				temp("t2"),
				temp("t3"),
				op(OpType.ADD, temp("t4"), temp("t4")),
				temp("t5"),
				temp("t6"),
				temp("t7"),
				temp("t8")
				);
		
		IRMove movRet2 = new IRMove(temp("t9"), temp("_RET0"));
		IRMove movRet3 = new IRMove(temp("t10"), temp("_RET1"));
		
		IRSeq seq = new IRSeq(call, movRet2, movRet3, ret());
		
		tile(seq);
	}

	@Test
	void tileCall() {
		IRCallStmt call = new IRCallStmt(new IRName("f"), 0, 
				temp("t1"),
				temp("t2"),
				temp("t3"),
				op(OpType.ADD, temp("t4"), temp("t4")),
				temp("t5"),
				temp("t6")
				);
		
		IRMove movRet2 = new IRMove(temp("t9"), temp("_RET0"));
		IRMove movRet3 = new IRMove(temp("t10"), temp("_RET1"));
		
		IRSeq seq = new IRSeq(call, movRet2, movRet3, ret());
		
		tile(seq);
	}

	private Assem tile(IRNode node) {
		System.out.println("Before\n=========\n" + node);
		Tiler tiler = new Tiler(new IRNodeFactory_c());
		IRNode result = tiler.visit(node);
		System.out.println("After\n=========\n" + result.getAssem());
		System.out.println();

		return result.getAssem();
	}
}