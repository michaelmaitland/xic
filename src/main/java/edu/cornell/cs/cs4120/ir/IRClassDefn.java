package edu.cornell.cs.cs4120.ir;

import java.util.List;

import mtm68.util.ArrayUtils;

/** An IR class definition */
public class IRClassDefn {
    private String className;
    private List<IRFuncDefn> methods;

    public IRClassDefn() {
    	this.methods = ArrayUtils.empty();
    }
    
    public IRClassDefn(String className, List<IRFuncDefn> methods) {
    	this.className = className;
    	this.methods = methods;
    }

	public String getClassName() {
		return className;
	}

	public List<IRFuncDefn> getMethods() {
		return methods;
	}
}
