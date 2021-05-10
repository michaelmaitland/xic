package mtm68.ir.cfg;

import edu.cornell.cs.cs4120.ir.IRStmt;

public class IRData<T> {

	private IRStmt ir;
	private T flowData;
	
	public IRData(IRStmt ir, T flowData) {
		this.ir= ir;
		this.flowData = flowData;
	}
	
	public IRStmt getIR() {
		return ir;
	}
	
	public T getFlowData() {
		return flowData;
	}
	
	public void setFlowData(T flowData) {
		this.flowData = flowData;
	}
	
	@Override
	public String toString() {
		return ir.toString();
	}
}
