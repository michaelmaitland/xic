package mtm68.ir;

import static mtm68.ir.IRTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRCompUnit;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import mtm68.assem.Assem;
import mtm68.assem.CmpAssem;
import mtm68.assem.CqoAssem;
import mtm68.assem.IDivAssem;
import mtm68.assem.JumpAssem.JumpType;
import mtm68.assem.MulAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.SetccAssem;
import mtm68.assem.SetccAssem.CC;
import mtm68.assem.cfg.Liveness;
import mtm68.assem.op.AddAssem;
import mtm68.assem.operand.RealReg;
import mtm68.ir.cfg.AvailableExpressions;
import mtm68.util.ArrayUtils;
import mtm68.util.SetUtils;

public class AvailableExpressionsTests {
	
	@Test
	void testSimple() throws IOException {
		
		List<IRStmt> func = ArrayUtils.elems(
				move(temp("t1"), constant(1)),
				move(temp("t2"), constant(2))
			);
		
		perform(func);
	}

	private void perform(List<IRStmt> stmts) throws IOException {
		IRSeq seq = new IRSeq(stmts);
		IRFuncDefn func = new IRFuncDefn("f", seq, 0);
		
		Map<String, IRFuncDefn> funcs = new HashMap<>();
		funcs.put("f", func);
		IRCompUnit comp = new IRCompUnit("test.xi", funcs);

		AvailableExpressions ae = new AvailableExpressions();
		ae.performAvaliableExpressionsAnalysis(comp, new IRNodeFactory_c());
		
		ae.getGraph();

		ae.showGraph(new PrintWriter(System.out));
	}
}
