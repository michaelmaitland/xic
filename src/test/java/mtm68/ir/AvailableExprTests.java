package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.ADD;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.EQ;
import static mtm68.ir.IRTestUtils.call;
import static mtm68.ir.IRTestUtils.cjump;
import static mtm68.ir.IRTestUtils.constant;
import static mtm68.ir.IRTestUtils.label;
import static mtm68.ir.IRTestUtils.mem;
import static mtm68.ir.IRTestUtils.move;
import static mtm68.ir.IRTestUtils.op;
import static mtm68.ir.IRTestUtils.temp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.AvailableExprs;
import mtm68.util.ArrayUtils;

public class AvailableExprTests {
	
	@Test
	void constsAndTempDontGen() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t2"), temp("t3"))
			);
		perform(func);
	}

	@Test
	void testXGetsESameTemp() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(ADD, constant(1), constant(2))),
				move(temp("t1"), op(ADD, constant(3), constant(4)))
			);
		
		// Second node should contain both binops
		perform(func);
	}
	
	@Test
	void testXGetsESameTempSubexpr() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(ADD, op(ADD, constant(1), constant(2)), constant(3))),
				move(temp("t1"), op(ADD, constant(4), constant(5)))
			);
		
		// Second node should contain both binops and nested binop
		perform(func);
	}
	
	@Test
	void testXGetsEKillTemp() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), op(ADD, temp("y"), constant(1))),
				move(temp("y"), constant(2))
			);
		
		// Second node out should have killed the add because it contains y
		perform(func);
	}
	
	@Test
	void testMemOnlyOnLeftKillsAllMem() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(mem(temp("t1")), mem(temp("t2"))),
				move(mem(temp("t3")), constant(3))
			);
		
		// Third node out should kill both mems from the zeroth node
		perform(func);
	}
	
	@Test
	void testMemOnlyOnRightDoesNotKill() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(mem(temp("t3")), op(ADD, constant(1), constant(2))),
				move(temp("t1"), mem(temp("t2")))
			);
		
		// second node out should not kill the mem from n0
		perform(func);
	}
	
	@Test
	void testMemE1GetsMemE2Out() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(ADD, constant(1), constant(1))),
				move(mem(temp("t2")), mem(temp("t3")))
			);
		
		// Second node out should have [e1], [e2], and all subexprs
		perform(func);
	}
	
	@Test
	void testMemE1GetsMemE2OutSubexpr() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(mem(op(ADD, temp("t2"), temp("t3"))), mem(op(ADD, constant(1), temp("t4"))))
			);
		
		// Second node out should have [e1], [e2], and all subexprs
		perform(func);
	}
	
	@Test
	void testXGetsFGenEArgsAreTemps() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, temp("t2"), temp("4")),
				move(temp("x"), temp("_RET0"))
			);
		
		perform(func);
	}
	
	@Test
	void testXGetsFGenEArgIsBinop() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, op(ADD, temp("t2"), temp("4"))),
				move(temp("x"), temp("_RET0"))
			);
		
		perform(func);
	}
	
	@Test
	void testXGetsFGenEArgsWithNestedSubexpr() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, op(ADD, op(ADD,constant(1),temp("t2")), temp("4"))),
				move(temp("x"), temp("_RET0"))
			);
		
		perform(func);
	}
	
	
	@Test
	void testXGetsFGenEMultiArg() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				call("f", 1, op(ADD, temp("t2"), temp("t4")), op(ADD, temp("t3"), temp("t5"))),
				move(temp("x"), temp("_RET0"))
			);
		
		perform(func);
	}
	
	@Test
	void testIfGenE() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				cjump(op(EQ, constant(1), constant(2)), "l1", "l2"),
				label("l1"),
				move(temp("t1"), constant(1)),
				label("l2"),
				move(temp("t2"), constant(2))
			);
		
		// Second and third nodes should have the same in and out as the out from the first
		perform(func);
	}
	

	@Test
	void testIfKillsNothing() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t2"), temp("t1")),
				move(temp("t1"), temp("t3")),
				cjump(op(EQ, constant(1), constant(2)), "l1", "l2"),
				label("l1"),
				move(temp("t1"), constant(1)),
				label("l2"),
				move(temp("t2"), constant(2))
			);
		
		// Second and third nodes should have the same in and out as the out from the first
		perform(func);
	}

	private void perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);

		AvailableExprs ae = new AvailableExprs(func, new IRNodeFactory_c());
		ae.performAvaliableExpressionsAnalysis();
		
		ae.getGraph();

		ae.showGraph(new PrintWriter(System.out));
	}
}
