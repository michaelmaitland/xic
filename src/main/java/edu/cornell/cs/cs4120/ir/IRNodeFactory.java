package edu.cornell.cs.cs4120.ir;

import java.util.List;
import java.util.Map;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRMem.MemType;

public interface IRNodeFactory {

    IRBinOp IRBinOp(OpType type, IRExpr left, IRExpr right);

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    IRCall IRCall(IRExpr target, IRExpr... args);

    /**
     *
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    IRCall IRCall(IRExpr target, List<IRExpr> args);

    /**
     * Construct a CJUMP instruction with fall-through on false.
     * @param expr the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     */
    IRCJump IRCJump(IRExpr expr, String trueLabel);

    /**
     *
     * @param expr the condition for the jump
     * @param trueLabel the destination of the jump if {@code expr} evaluates
     *          to true
     * @param falseLabel the destination of the jump if {@code expr} evaluates
     *          to false
     */
    IRCJump IRCJump(IRExpr expr, String trueLabel, String falseLabel);

    IRCompUnit IRCompUnit(String name);

    IRCompUnit IRCompUnit(String name, Map<String, IRFuncDefn> functions);

    IRCompUnit IRCompUnit(String name, Map<String, IRFuncDefn> functions, Map<String, IRData> dataMap);

    /**
     *
     * @param value value of this constant
     */
    IRConst IRConst(long value);

    /**
     *
     * @param stmt IR statement to be evaluated for side effects
     * @param expr IR expression to be evaluated after {@code stmt}
     */
    IRESeq IRESeq(IRStmt stmt, IRExpr expr);

    /**
     *
     * @param expr the expression to be evaluated and result discarded
     */
    IRExp IRExp(IRExpr expr);

    IRFuncDefn IRFuncDefn(String name, IRStmt stmt, int numArgs);

    IRClassDefn IRClassDefn(String className, List<IRFuncDefn> methods);

    /**
     *
     * @param expr the destination of the jump
     */
    IRJump IRJump(IRExpr expr);

    /**
     *
     * @param name name of this memory address
     */
    IRLabel IRLabel(String name);

    /**
     *
     * @param expr the address of this memory location
     */
    IRMem IRMem(IRExpr expr);
    
    /**
     * 
     * @param expr the address of this memory location
     * @param memType the type of memory
     */
    IRMem IRMem(IRExpr expr, MemType memType);

    /**
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    IRCallStmt IRCallStmt(IRExpr target, List<IRExpr> args);

    /**
     * @param target address of the code for this function call
     * @param args arguments of this function call
     */
    IRCallStmt IRCallStmt(IRExpr target, IRExpr... args);

    IRCallStmt IRCallStmt(IRExpr target, int numRets, List<IRExpr> args);

    /**
     *
     * @param target the destination of this move
     * @param expr the expression whose value is to be moved
     */
    IRMove IRMove(IRExpr target, IRExpr expr);

    /**
     *
     * @param name name of this memory address
     */
    IRName IRName(String name);

    /**
     * @param rets values to return
     */
    IRReturn IRReturn(List<IRExpr> rets);

    /**
     * @param rets values to return
     */
    IRReturn IRReturn(IRExpr ...rets);

    /**
     * @param stmts the statements
     */
    IRSeq IRSeq(IRStmt... stmts);

    /**
     * Create a SEQ from a list of statements.
     * The list should not be modified subsequently.
     * @param stmts the sequence of statements
     */
    IRSeq IRSeq(List<IRStmt> stmts);

    /**
     *
     * @param name name of this temporary register
     */
    IRTemp IRTemp(String name);

    IRData IRData(String name, long[] data);
}
