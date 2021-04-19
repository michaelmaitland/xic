package edu.cornell.cs.cs4120.ir;

import edu.cornell.cs.cs4120.ir.visit.InsnMapsBuilder;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.LabelAssem;

/**
 * An intermediate representation for naming a memory address
 */
public class IRLabel extends IRStmt {
    private String name;
    
    private boolean used;

    /**
     *
     * @param name name of this memory address
     */
    public IRLabel(String name) {
        this.name = name;
        this.used = false;
    }

    public String name() {
        return name;
    }

    @Override
    public String label() {
        return "LABEL(" + name + ")";
    }

    @Override
    public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
        v.addNameToCurrentIndex(name);
        return v;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("LABEL");
        p.printAtom(name);
        p.endList();
    }
    
    /** Make sure this comes after a copy */
    public void setUsed(boolean used) {
   	 this.used = used;
	}
    
    public boolean isUsed() {
		return used;
	}
    
    @Override
   public IRNode unusedLabels(UnusedLabelVisitor v) {
   	 // This is so the visitor can freely call setUsed without
   	 // having to worry about copying
   	 IRLabel newLabel = copy();

   	 v.recordLabel(newLabel);
   	 
   	return newLabel;  
   }

	@Override
	public IRNode lower(Lowerer v) {
		return this;
	}

	@Override
	public IRNode tile(Tiler t) {
		return copyAndSetAssem(new LabelAssem(name));
	}
}
