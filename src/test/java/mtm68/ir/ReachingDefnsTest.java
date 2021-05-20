package mtm68.ir;

import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.ADD;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.MUL;
import static edu.cornell.cs.cs4120.ir.IRBinOp.OpType.SUB;
import static mtm68.ir.IRTestUtils.cjump;
import static mtm68.ir.IRTestUtils.constant;
import static mtm68.ir.IRTestUtils.jump;
import static mtm68.ir.IRTestUtils.label;
import static mtm68.ir.IRTestUtils.mem;
import static mtm68.ir.IRTestUtils.move;
import static mtm68.ir.IRTestUtils.op;
import static mtm68.ir.IRTestUtils.ret;
import static mtm68.ir.IRTestUtils.temp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.ir.cfg.ReachingDefns;
import mtm68.util.ArrayUtils;

public class ReachingDefnsTest {
	
	@Test
	void prelim2Example() throws IOException {
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), constant(0)),
				move(temp("z"), constant(10)),
				move(temp("z"), op(SUB, temp("z"), constant(1))),
				label("header"),
				move(temp("z"), op(ADD, temp("z"), constant(1))),
				move(temp("y"), temp("x")),
				cjump("lt", "lf"),
				label("lt"),
				move(temp("w"), op(MUL, temp("x"), temp("y"))),
				jump("merge"),
				label("lf"),
				move(temp("w"), temp("y")),
				move(temp("y"), mem(op(ADD, temp("x"), constant(4)))),
				jump("merge"),
				move(temp("x"), op(SUB, temp("x"), temp("w"))),
				cjump("header", "fallthrough"),
				label("fallthrough"),
				ret()
			);

		perform(func);
	}

	
	@Test
	void singleReach() throws IOException {
	List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), temp("y")),
				move(temp("z"), op(ADD, op(MUL, constant(2), temp("x")), constant(1)))
			);

		perform(func);
	}
	
	@Test
	void redefine() throws IOException {
	List<IRStmt> func = ArrayUtils.elems(
				move(temp("x"), temp("y")),
				move(temp("x"), temp("w")),
				move(temp("z"), op(ADD, op(MUL, constant(2), temp("x")), constant(1)))
			);

		perform(func);
	}
	
	@Test
	void loop() throws IOException {
	List<IRStmt> func = ArrayUtils.elems(
				label("h"),
				move(temp("x"), temp("y")),
				move(temp("z"), op(ADD, op(MUL, constant(2), temp("x")), constant(1))),
				cjump("h", "ft"),
				label("ft"),
				ret()
			);

		perform(func);
	}
	
	@Test
	void loopExitKills() throws IOException {
	List<IRStmt> func = ArrayUtils.elems(
				label("h"),
				move(temp("x"), temp("y")),
				move(temp("z"), op(ADD, op(MUL, constant(2), temp("x")), constant(1))),
				cjump("h", "ft"),
				label("ft"),
				move(temp("z"), temp("q")),
				ret()
			);

		perform(func);
	}

	private void perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);

		ReachingDefns rd = new ReachingDefns(func);
		rd.performReachingDefnsAnalysis();
		
		rd.getGraph();

		rd.showGraph(new PrintWriter(System.out));
	}
}
