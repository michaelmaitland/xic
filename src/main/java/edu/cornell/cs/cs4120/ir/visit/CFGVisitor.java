package edu.cornell.cs.cs4120.ir.visit;

import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import mtm68.ir.cfg.CFGBuilder;

public class CFGVisitor extends IRVisitor {
	
	private CFGBuilder builder;

	public CFGVisitor(IRNodeFactory inf) {
		super(inf);
		builder = new CFGBuilder();
	}
	
	protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
		return n_.doControlFlow(builder);
	}

	@Override
	protected IRNode override(IRNode parent, IRNode n) {
		// Don't visit expressions
		if(n instanceof IRExpr) return n;
		
		return null;
	}

}
