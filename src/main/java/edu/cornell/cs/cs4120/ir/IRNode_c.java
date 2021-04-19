package edu.cornell.cs.cs4120.ir;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckCanonicalIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.CheckConstFoldedIRVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.InsnMapsBuilder;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.ir.visit.UnusedLabelVisitor;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.Assem;
import mtm68.assem.SeqAssem;
import mtm68.ir.cfg.CFGBuilder;
import mtm68.util.ArrayUtils;

/**
 * A node in an intermediate-representation abstract syntax tree.
 */
public abstract class IRNode_c implements IRNode, Cloneable {

	protected Assem assem;
	
	@Override
	public IRNode visitChildren(IRVisitor v) {
		return this;
	}

	@Override
	public <T> T aggregateChildren(AggregateVisitor<T> v) {
		return v.unit();
	}

	@Override
	public InsnMapsBuilder buildInsnMapsEnter(InsnMapsBuilder v) {
		return v;
	}

	@Override
	public IRNode buildInsnMaps(InsnMapsBuilder v) {
		v.addInsn(this);
		return this;
	}

	@Override
	public CheckCanonicalIRVisitor checkCanonicalEnter(CheckCanonicalIRVisitor v) {
		return v;
	}

	@Override
	public boolean isCanonical(CheckCanonicalIRVisitor v) {
		return true;
	}

	@Override
	public boolean isConstFolded(CheckConstFoldedIRVisitor v) {
		return true;
	}
	
	@Override
	public IRNode tile(Tiler t) {
		return this;
	}

	@Override
	public abstract String label();

	@Override
	public abstract void printSExp(SExpPrinter p);

	public IRNode doControlFlow(CFGBuilder builder) {
		return this;
	}

	@Override
	public IRNode unusedLabels(UnusedLabelVisitor v) {
		return this;
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw); SExpPrinter sp = new CodeWriterSExpPrinter(pw)) {
			printSExp(sp);
		}
		return sw.toString();
	}

	@SuppressWarnings("unchecked")
	public <N extends IRNode_c> N copy() {
		try {
			return (N) clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public <N extends IRNode_c> N copyAndSetAssem(Assem assem) {
		N newN = this.copy();
		newN.assem = assem;
		return newN;
	}

	public void setAssem(Assem assem) {
		this.assem = assem;
	}

	@Override
	public Assem getAssem() {
		return assem;
	}
	
	@Override
	public void appendAssems(List<Assem> assems) {
		assem = new SeqAssem(ArrayUtils.prepend(assem, assems));
	}
}
