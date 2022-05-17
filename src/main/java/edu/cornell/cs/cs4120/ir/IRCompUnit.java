package edu.cornell.cs.cs4120.ir;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.ir.visit.IRConstantFolder;
import edu.cornell.cs.cs4120.ir.visit.IRContainsExprWithSideEffect;
import edu.cornell.cs.cs4120.ir.visit.IRContainsMemSubexprDecorator;
import edu.cornell.cs.cs4120.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.ir.visit.Lowerer;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import edu.cornell.cs.cs4120.util.InternalCompilerError;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.assem.CompUnitAssem;
import mtm68.assem.FuncDefnAssem;
import mtm68.util.SetUtils;

/**
 * An intermediate representation for a compilation unit
 */
public class IRCompUnit extends IRNode_c {
    private String name;
    private Map<String, IRFuncDefn> functions;
    private List<String> ctors;
    private Map<String, IRData> dataMap;

    public IRCompUnit(String name) {
        this(name, new LinkedHashMap<>(), new ArrayList<>(), new LinkedHashMap<>());
    }

    public IRCompUnit(String name, Map<String, IRFuncDefn> functions) {
        this(name, functions, new ArrayList<>(), new LinkedHashMap<>());
    }
    
    public IRCompUnit(String name, Map<String, IRFuncDefn> functions, List<String> ctors, Map<String, IRData> dataMap) {
        this.name = name;
        this.functions = functions;
        this.ctors = ctors;
        this.dataMap = dataMap;
    }

    public void appendFunc(IRFuncDefn func) {
        functions.put(func.name(), func);
    }

    public void appendCtor(String functionName) {
        ctors.add(functionName);
    }

    public void appendData(IRData data) {
        dataMap.put(data.name(), data);
    }

    public String name() {
        return name;
    }

    public Map<String, IRFuncDefn> functions() {
        return functions;
    }

    public IRFuncDefn getFunction(String name) {
        return functions.get(name);
    }

    public List<String> ctors() {
        return ctors;
    }

    public Map<String, IRData> dataMap() {
        return dataMap;
    }

    public IRData getData(String name) {
        return dataMap.get(name);
    }
    
    public List<IRStmt> flattenCompUnit(){
    	List<IRStmt> stmts = new ArrayList<>();
    	for(IRFuncDefn fDefn : functions.values()) {
    		stmts.add(fDefn.body());
    	}
    	return IRUtils.flattenSeq(stmts);
    }

    @Override
    public String label() {
        return "COMPUNIT";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        Map<String, IRFuncDefn> results = new LinkedHashMap<>();
        for (IRFuncDefn func : functions.values()) {
            IRFuncDefn newFunc = (IRFuncDefn) v.visit(this, func);
            if (newFunc != func) modified = true;
            results.put(newFunc.name(), newFunc);
        }
        
        if (modified) return v.nodeFactory().IRCompUnit(name, results, dataMap);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRFuncDefn func : functions.values())
            result = v.bind(result, v.visit(func));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("COMPUNIT");
        p.printAtom(name);
        for (String ctor : ctors) {
            p.printAtom(ctor);
        }
        for (IRData data : dataMap.values()) {
            p.printAtom("DATA");
            p.printAtom(data.name());
            p.startList();
            for (long value : data.data()) {
                p.printAtom(String.valueOf(value));
            }
            p.endList();
        }
        for (IRFuncDefn func : functions.values())
            func.printSExp(p);
        p.endList();
    }

	@Override
	public IRNode lower(Lowerer v) {
		return this;
	}

	@Override
	public IRNode constantFold(IRConstantFolder v) {
		return this;
	}
	
	@Override 
	public IRNode tile(Tiler t) {
		List<FuncDefnAssem> funcAssems = new ArrayList<>();
        for (IRFuncDefn func : functions.values()) {
            funcAssems.add((FuncDefnAssem)func.getAssem());
        }

        return copyAndSetAssem(new CompUnitAssem(name, funcAssems));	
	}

	@Override
	public Set<IRExpr> genAvailableExprs() {
		Set<IRExpr> exprs = SetUtils.empty();
		for(IRFuncDefn fun : functions.values()) {
			exprs = SetUtils.union(exprs, fun.genAvailableExprs());
		}
		return exprs;
	}
	
	@Override
	public Set<IRTemp> use() {
		Set<IRTemp> temps = SetUtils.empty();
		for(IRFuncDefn fun : functions.values()) {
			temps = SetUtils.union(temps, fun.use());
		}
		return temps;
	}

	@Override
	public boolean containsExpr(IRExpr expr) {
		return functions.values().stream()
				   .map(e -> e.containsExpr(expr))
				   .reduce(Boolean.FALSE, Boolean::logicalOr);
	}
	
	@Override
	public IRNode replaceExpr(IRExpr toReplace, IRExpr replaceWith) {
		throw new InternalCompilerError("not replace IRCompUnit");
	}

	@Override
	public IRNode decorateContainsMutableMemSubexpr(IRContainsMemSubexprDecorator irContainsMemSubexpr) {
		boolean b = functions.values().stream()
				   .map(IRNode::doesContainsMutableMemSubexpr)
				   .reduce(Boolean.FALSE, Boolean::logicalOr);
		
		IRCompUnit copy = copy();
		copy.setContainsMutableMemSubexpr(b);
		return copy;
	}

	public void setFunctions(Map<String, IRFuncDefn> newFuncs) {
		this.functions = newFuncs;
	}

	@Override
	public IRNode decorateContainsExprWithSideEffect(IRContainsExprWithSideEffect irContainsExprWithSideEffect) {
		boolean b = functions.values().stream()
				   .map(IRNode::doesContainsExprWithSideEffect)
				   .reduce(Boolean.FALSE, Boolean::logicalOr);
		
		IRCompUnit copy = copy();
		copy.setContainsExprWithSideEffect(b);
		return copy;
	}
}
