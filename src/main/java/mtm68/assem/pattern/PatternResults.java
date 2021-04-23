package mtm68.assem.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRExpr;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Reg;
import polyglot.util.InternalCompilerError;

public class PatternResults {
	
	private Map<String, IRExpr> matchedExprs;
	private List<IRExpr> usedExprs;
	
	public PatternResults(Map<String, IRExpr> matchedExprs) {
		this.matchedExprs = matchedExprs;

		usedExprs = new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PatternMatch> T get(String name, Class<T> clazz) {
		IRExpr expr = matchedExprs.get(name);
		
		if(Reg.class.isAssignableFrom(clazz)) {
			usedExprs.add(expr);
			return (T) expr.getResultReg(); 
		} else if(Imm.class.isAssignableFrom(clazz)) {
			
			if(!(expr instanceof IRConst)) throw new InternalCompilerError("Cannot extract an immediate value out of " + expr.getClass());
			
			return (T) new Imm(((IRConst)expr).constant());
		}

		throw new InternalCompilerError("Invalid class " + clazz + " for pattern extraction");
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IRExpr> T getExpr(String name) {
		return (T)matchedExprs.get(name);
	}
	
	public boolean containsKey(String name) {
		return matchedExprs.containsKey(name);
	}
	
	public List<IRExpr> getUsedExprs() {
		return usedExprs;
	}
}
