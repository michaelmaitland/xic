package mtm68.assem.pattern;

import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRExpr;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Reg;
import polyglot.util.InternalCompilerError;

public class PatternResults {
	
	private Map<String, IRExpr> matchedExprs;
	
	public PatternResults(Map<String, IRExpr> matchedExprs) {
		this.matchedExprs = matchedExprs;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PatternMatch> T get(String name, Class<T> clazz) {
		IRExpr expr = matchedExprs.get(name);
		
		if(Reg.class.isAssignableFrom(clazz)) {
			return (T) expr.getResultReg(); 
		} else if(Imm.class.isAssignableFrom(clazz)) {
			
			if(!(expr instanceof IRConst)) throw new InternalCompilerError("Cannot extract an immediate value out of " + expr.getClass());
			
			return (T) new Imm(((IRConst)expr).constant());
		}

		throw new InternalCompilerError("Invalid class " + clazz + " for pattern extraction");
	}

}
