package edu.cornell.cs.cs4120.ir.visit;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExp;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.util.ArrayUtils;

public class Lowerer extends IRVisitor {

	public Lowerer(IRNodeFactory inf) {
		super(inf);
	}
	
	@Override
	public IRVisitor enter(IRNode parent, IRNode n) {
		return this;
	}

	@Override
	public IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {		
		return n_.lower(this);
	}
	
	public IRNode prependSideEffectsToStmt(IRStmt stmt, List<IRStmt> sideEffects) {
		sideEffects.add(stmt);
		return new IRSeq(sideEffects);
	}
	
	public List<IRStmt> getESeqSideEffects(IRStmt stmt, List<IRStmt> innerSideEffects) {
		List<IRStmt> sideEffects = new ArrayList<>();
		if(stmt instanceof IRSeq) sideEffects.addAll(((IRSeq)stmt).stmts());
		else sideEffects.add(stmt);
		sideEffects.addAll(innerSideEffects);
		return sideEffects;
	}
	
	public List<IRStmt> flattenSeq(List<IRStmt> stmts) {
		List<IRStmt> newSeq = new ArrayList<>();
		for(IRStmt stmt : stmts) {
			if(stmt instanceof IRSeq) newSeq.addAll(flattenSeq(((IRSeq)stmt).stmts()));
			else newSeq.add(stmt);		
		}
		return newSeq;
	}
	
	public IRSeq transformCall(IRExpr func, List<IRExpr> args) {
		List<IRStmt> callStmts = new ArrayList<>();
		List<IRExpr> newArgs = new ArrayList<>();
		
		for(int i = 0; i < args.size(); i++) {
			IRExpr arg = args.get(i);
			callStmts.addAll(arg.getSideEffects());
			String argName = "_ARG" + i;
			callStmts.add(new IRMove(new IRTemp(argName), arg));
			newArgs.add(new IRTemp(argName));
		}
				
		IRCallStmt newCall = new IRCallStmt(func, newArgs); 
		callStmts.add(newCall);
		return new IRSeq(callStmts);
	}
	
	public IRSeq transformReturn(List<IRExpr> rets) {
		List<IRStmt> retStmts = new ArrayList<>();
		
		for(int i = 0; i < rets.size(); i++) {
			IRExpr ret = rets.get(i);
			retStmts.addAll(ret.getSideEffects());
			String argName = "_RET" + i;
			retStmts.add(new IRMove(new IRTemp(argName), ret));
		}
				
		IRReturn newRet = new IRReturn(ArrayUtils.empty());
		retStmts.add(newRet);
		return new IRSeq(retStmts);
	}
	
	public IRBinOp transformBinOpGeneral(OpType type, IRExpr left, IRExpr right) {
		List<IRStmt> sideEffects = new ArrayList<>();
		sideEffects.addAll(left.getSideEffects());
		
		//TODO maybe commute case?
		if(!right.getSideEffects().isEmpty()) { 
			String tempName = getFreshTemp();
			sideEffects.add(new IRMove(new IRTemp(tempName), left));
			left = new IRTemp(tempName);
		}
		sideEffects.addAll(right.getSideEffects());
		IRBinOp newOp = new IRBinOp(type, left, right);
		newOp.setSideEffects(sideEffects);
		return newOp;
	}
	
	public IRBinOp transformBinOpCommute(OpType type, IRExpr left, IRExpr right) {
		//TODO
		return null;
	}
	
	public IRSeq transformMoveGeneral(IRExpr target, IRExpr source) {
		List<IRStmt> moveStmts = new ArrayList<>();
		moveStmts.addAll(target.getSideEffects());
		
		//TODO maybe commute case?
		if(!source.getSideEffects().isEmpty() && !(target instanceof IRTemp)) {
			String tempName = getFreshTemp();
			moveStmts.add(new IRMove(new IRTemp(getFreshTemp()), ((IRMem)target).expr()));
			target = new IRMem(new IRTemp(tempName));
		}
		moveStmts.addAll(source.getSideEffects());
		
		IRMove newMove = new IRMove(target, source);
		moveStmts.add(newMove);
		
		return new IRSeq(moveStmts);
	}
	
	public IRSeq transformMoveCommute(IRExpr target, IRExpr source) {
		//TODO
		return null;
	}
	
	private String getFreshTemp() {
		//TODO
		return "t";
	}
}
