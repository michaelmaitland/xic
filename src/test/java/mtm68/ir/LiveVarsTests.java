package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.ADD;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.DIV;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.EQ;
import static mtm68.ir.IRTestUtils.call;
import static mtm68.ir.IRTestUtils.cjump;
import static mtm68.ir.IRTestUtils.constant;
import static mtm68.ir.IRTestUtils.jump;
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
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.LiveVariables;
import mtm68.util.ArrayUtils;

public class LiveVarsTests {
	
	
	@Test
	void testSimple() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), temp("t2")),
				move(temp("t3"), temp("t2")),
				move(temp("t2"), temp("t4")),
				move(temp("t8"), temp("t5")),
				move(temp("t9"), temp("t10"))
			);
		perform(func);
	}
	
	@Test
	void xGetsEGen() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t2"), temp("t1"))
			);
		perform(func);
	}
	
	@Test
	void divisionIsAlwaysLive() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), op(DIV, constant(1), constant(2))),
				move(temp("t2"), temp("t3"))
			);
		perform(func);
	}
	
	@Test
	void xGetsEKill() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t1"), temp("t3"))
			);
		perform(func);
	}
	
	@Test
	void xGetsEGenUnused() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t3"), temp("t2"))
			);
		perform(func);
	}
	
	@Test
	void memE1GetsE2() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(mem(temp("t1")), temp("t2")),
				move(temp("t3"), temp("t1")),
				move(temp("t4"), temp("t2"))
			);
		perform(func);
	}
	
	@Test
	void memE1GetsE2KillsNothing() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t3"), temp("t1")),
				move(temp("t4"), temp("t2")),
				move(mem(temp("t1")), temp("t2"))
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
	void ifEGenE() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				cjump(op(EQ, temp("t1"), constant(1)), "t", "f"),
				label("f"),
				move(temp("t3"), temp("t2")),
				jump("m"),
				label("t"),
				move(temp("t4"), temp("t5")),
				label("m"),
				move(temp("t6"), temp("t5"))
			);
		perform(func);
	}

	private void perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);

		LiveVariables lv = new LiveVariables(func);
		lv.performAnalysis();
		
		lv.getGraph();

		lv.showGraph(new PrintWriter(System.out));
	}
}
