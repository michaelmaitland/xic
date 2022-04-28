package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRMem.MemType;

public class IRNodeFactory_c implements IRNodeFactory {

    @Override
    public IRBinOp IRBinOp(OpType type, IRExpr left, IRExpr right) {
        return new IRBinOp(type, left, right);
    }

    @Override
    public IRCall IRCall(IRExpr target, IRExpr... args) {
        return new IRCall(target, args);
    }

    @Override
    public IRCall IRCall(IRExpr target, List<IRExpr> args) {
        return new IRCall(target, args);
    }

    @Override
    public IRCJump IRCJump(IRExpr expr, String trueLabel) {
        return new IRCJump(expr, trueLabel);
    }

    @Override
    public IRCJump IRCJump(IRExpr expr, String trueLabel, String falseLabel) {
        return new IRCJump(expr, trueLabel, falseLabel);
    }

    @Override
    public IRCompUnit IRCompUnit(String name) {
        return new IRCompUnit(name);
    }

    @Override
    public IRCompUnit IRCompUnit(String name,
            Map<String, IRFuncDefn> functions) {
        return new IRCompUnit(name, functions);
    }
    
    @Override
    public IRCompUnit IRCompUnit(String name,
            Map<String, IRFuncDefn> functions,  Map<String, IRClassDefn> classes) {
        return new IRCompUnit(name, functions, classes);
    }

    @Override
    public IRConst IRConst(long value) {
        return new IRConst(value);
    }

    @Override
    public IRESeq IRESeq(IRStmt stmt, IRExpr expr) {
        return new IRESeq(stmt, expr);
    }

    @Override
    public IRExp IRExp(IRExpr expr) {
        return new IRExp(expr);
    }

    @Override
    public IRFuncDefn IRFuncDefn(String name, IRStmt stmt, int numArgs) {
        return new IRFuncDefn(name, stmt, numArgs);
    }
    
    @Override
    public IRClassDefn IRClassDefn(String className, List<IRFuncDefn> methods, IRESeq dispatchVector) {
    	return new IRClassDefn(className, methods, dispatchVector);
    }

    @Override
    public IRJump IRJump(IRExpr expr) {
        return new IRJump(expr);
    }

    @Override
    public IRLabel IRLabel(String name) {
        return new IRLabel(name);
    }

    @Override
    public IRMem IRMem(IRExpr expr) {
        return new IRMem(expr);
    }
    
    @Override
	public IRMem IRMem(IRExpr expr, MemType memType) {
		return new IRMem(expr, memType);
	}

    @Override
    public IRCallStmt IRCallStmt(IRExpr target, int numRets, List<IRExpr> args) {
        return new IRCallStmt(target, numRets, args);
    }

    @Override
    public IRCallStmt IRCallStmt(IRExpr target, List<IRExpr> args) {
        return IRCallStmt(target, 0, args);
    }
    
    @Override
   public IRCallStmt IRCallStmt(IRExpr target, IRExpr... args) {
   	return new IRCallStmt(target, 0, args);
   }

    @Override
    public IRMove IRMove(IRExpr target, IRExpr expr) {
        return new IRMove(target, expr);
    }

    @Override
    public IRName IRName(String name) {
        return new IRName(name);
    }

    @Override
    public IRReturn IRReturn(List<IRExpr> rets) {
        return new IRReturn(rets);
    }

    @Override
    public IRReturn IRReturn(IRExpr ...rets) {
        return new IRReturn(rets);
    }

    @Override
    public IRSeq IRSeq(IRStmt... stmts) {
        return new IRSeq(stmts);
    }

    @Override
    public IRSeq IRSeq(List<IRStmt> stmts) {
        return new IRSeq(stmts);
    }

    @Override
    public IRTemp IRTemp(String name) {
        return new IRTemp(name);
    }
}
