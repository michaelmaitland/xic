package mtm68.assem.pattern;

import java.util.List;
import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRReturn;

public class ReturnPattern implements Pattern{
	
	private List<IRExpr> rets;

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRReturn)) return false;
		
		IRReturn ret = (IRReturn) node;
		rets = ret.rets();
		
		return true;
	}

	@Override
	public void addMatchedExprs(Map<String, IRExpr> exprs) {
		for(int i = 0; i < exprs.size(); i++) {
			exprs.put("ret" + i, rets.get(i));
		}
	}
}
