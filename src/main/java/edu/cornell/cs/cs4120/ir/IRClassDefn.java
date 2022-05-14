package edu.cornell.cs.cs4120.ir;

import java.util.List;

import mtm68.util.ArrayUtils;

/** An IR class definition */
public class IRClassDefn {
    private String className;
    private List<IRFuncDefn> methods;
    private IRData dispatchVector;

    public IRClassDefn() {
    	this.methods = ArrayUtils.empty();
    }
    
    public IRClassDefn(String className, List<IRFuncDefn> methods, IRData dispatchVector) {
    	this.className = className;
    	this.methods = methods;
    	this.dispatchVector = dispatchVector;
    }

	public String getClassName() {
		return className;
	}

	public List<IRFuncDefn> getMethods() {
		return methods;
	}

	public IRData getDispatchVector() {
		return dispatchVector;
	}
}
