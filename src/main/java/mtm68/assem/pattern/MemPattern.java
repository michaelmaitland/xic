package mtm68.assem.pattern;

import static mtm68.assem.pattern.Patterns.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRNode;
import mtm68.util.ArrayUtils;

public class MemPattern implements Pattern {
	
	private String name;
	private Map<String, IRExpr> matched;
	private List<Pattern> patterns;
	
	public MemPattern(String name) {
		this.name = name;
		matched = new HashMap<String, IRExpr>();
		
		String t1 = baseName();
		String t2 = indexName();
		String i = scaleName();
		String d = dispName();

		this.patterns = ArrayUtils.elems(
			// [$t1 + $i * $t2 + $d] 
			add(var(t1), add(mul(index(i), var(t2)), smallConstant(d))),

			// [$i * $t2 + $d] 
			add(mul(index(i), var(t2)), smallConstant(d)),

			// [$t1 + $i * $t2] 
			add(var(t1), mul(index(i), var(t2))),

			// [$t1 + $t2 + $d] 
			add(var(t1), add(var(t2), smallConstant(d))),

			// [$t1 + $d] 
			add(var(t1), smallConstant(d)),

			// [$t1 + $t2] 
			add(var(t1), var(t2)),

			// [$d] 
			smallConstant(d),

			// [$i * $t2] 
			mul(index(i), var(t2)),

			// [$t1] 
			var(t1)
		);
	}
	
	private String baseName() {
		return name + "_t1";
	}

	private String indexName() {
		return name + "_t2";
	}

	private String scaleName() {
		return name + "_i";
	}

	private String dispName() {
		return name + "_d";
	}

	@Override
	public boolean matches(IRNode node) {
		if(!(node instanceof IRMem)) return false;
		
		matched.clear();

		IRMem mem = (IRMem) node;
		
		boolean success = false;
		for(Pattern p : patterns) {
			if(p.matches(mem.expr())) {
				p.addMatchedExprs(matched);
				success = true;
				break;
			}
		}
		
		return success;
	}

	@Override
	public void addMatchedExprs(Map<String, IRExpr> exprs) {
		exprs.putAll(matched);
	}
}
