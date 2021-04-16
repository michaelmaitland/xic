package edu.cornell.cs.cs4120.ir.visit;

import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Reg;
import mtm68.util.FreshTempGenerator;

public class Tiler extends IRVisitor {

	public Tiler(IRNodeFactory inf) {
		super(inf);
	}
	
	@Override
    protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
        return n_.tile(this); 
    }
	
	public Reg getFreshAbstractReg() {
		return new AbstractReg(FreshTempGenerator.getFreshTemp());
	}
}
