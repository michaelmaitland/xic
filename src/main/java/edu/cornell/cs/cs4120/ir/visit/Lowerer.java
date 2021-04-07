package edu.cornell.cs.cs4120.ir.visit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cornell.cs.cs4120.ir.IRBinOp;
import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMem;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRReturn;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.util.ArrayUtils;
import mtm68.util.FreshTempGenerator;

public class Lowerer extends IRVisitor {
	
	public Lowerer(IRNodeFactory inf) {
		super(inf);
	}
	
	@Override
	public IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {		
		return n_.lower(this);
	}
	
	public IRSeq prependSideEffectsToStmt(IRStmt stmt, List<IRStmt> sideEffects) {
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
	
	public IRBinOp transformBinOp(IRBinOp binOp) {
		if(canCommute(binOp.left(), binOp.right())) 
			return transformBinOpCommute(binOp);
		else 
			return transformBinOpGeneral(binOp);
	}
	
	public IRBinOp transformBinOpGeneral(IRBinOp binOp) {
		IRExpr left = binOp.left();
		IRExpr right = binOp.right();
		
		List<IRStmt> sideEffects = new ArrayList<>();
		sideEffects.addAll(left.getSideEffects());
		
		String tempName = getFreshTemp();
		sideEffects.add(new IRMove(new IRTemp(tempName), left));
		left = new IRTemp(tempName);
		
		sideEffects.addAll(right.getSideEffects());
		IRBinOp newOp = binOp.copy();
		newOp.setLeft(left); 
		newOp.setSideEffects(sideEffects);
		return newOp;
	}
	
	public IRBinOp transformBinOpCommute(IRBinOp binOp) {
		IRExpr left = binOp.left();
		IRExpr right = binOp.right();
		
		List<IRStmt> sideEffects = new ArrayList<>();
		sideEffects.addAll(left.getSideEffects());
		sideEffects.addAll(right.getSideEffects());
		
		IRBinOp newOp = binOp.copy();
		newOp.setSideEffects(sideEffects);
		return newOp;
	}
	
	public IRSeq transformMove(IRMove move) {
		if(canCommute(move.target(), move.source()) || move.target() instanceof IRTemp)
			return transformMoveCommute(move);
		else
			return transformMoveGeneral(move);
	}
	
	public IRSeq transformMoveGeneral(IRMove move) {
		IRExpr target = move.target();
		IRExpr source = move.source();
		
		List<IRStmt> moveStmts = new ArrayList<>();
		moveStmts.addAll(target.getSideEffects());
		
		String tempName = getFreshTemp();
		moveStmts.add(new IRMove(new IRTemp(tempName), ((IRMem)target).expr()));
		target = new IRMem(new IRTemp(tempName));
		
		moveStmts.addAll(source.getSideEffects());
		
		IRMove newMove = move.copy();
		newMove.setTarget(target);
		moveStmts.add(newMove);
		
		return new IRSeq(moveStmts);
	}
	
	public IRSeq transformMoveCommute(IRMove move) {
		IRExpr target = move.target();
		IRExpr source = move.source();
		
		List<IRStmt> moveStmts = new ArrayList<>();
		moveStmts.addAll(target.getSideEffects());
		moveStmts.addAll(source.getSideEffects());
		
		moveStmts.add(move);	
		
		return new IRSeq(moveStmts);
	}
	
	
	// Need to make sure stmts in e2 dont write to temps in e1 expr
	// If mem used in both, don't commute
	private boolean canCommute(IRExpr e1, IRExpr e2) {
		boolean e1UsesMem = usesMem(e1);

		Set<String> exprTemps = new HashSet<>();
		getExprTemps(exprTemps, e1);
				
		for(IRStmt stmt : e2.getSideEffects()) {
			if(!canCommute(exprTemps, stmt) || e1UsesMem && writesToMem(stmt)) 
				return false;
		}
		
		return true;
	}
	
	private boolean usesMem(IRExpr expr) {
		if(expr instanceof IRBinOp) {
			return usesMem(((IRBinOp) expr).left()) ||
				usesMem(((IRBinOp) expr).right());
		}
		else if(expr instanceof IRCall) {
			boolean targetMem = usesMem(((IRCall) expr).target());
			for(IRExpr arg : ((IRCall) expr).args()) 
				if(targetMem || usesMem(arg)) return true;
			return false;
		}
		else if(expr instanceof IRMem) {
			return true;
		}
		return false;
	}
	
	private boolean writesToMem(IRStmt stmt) {
		if(stmt instanceof IRMove) {
			IRMove move = (IRMove) stmt;
			if(move.target() instanceof IRMem) {
				return true;
			}
		}
		else if(stmt instanceof IRSeq) {
			for(IRStmt s : ((IRSeq) stmt).stmts()) {
				if(writesToMem(s)) return true;
			}
		}
		return false;
	}
		
	private boolean canCommute(Set<String> exprTemps, IRStmt stmt) {
		if(stmt instanceof IRMove) {
			IRMove move = (IRMove) stmt;
			if(move.target() instanceof IRTemp) {
				IRTemp temp = (IRTemp) move.target();
				if(exprTemps.contains(temp.name())) return false;
			}
		}
		else if(stmt instanceof IRSeq) {
			for(IRStmt s : ((IRSeq) stmt).stmts()) {
				if(!canCommute(exprTemps, s)) return false;
			}
		}
		//Conservatively assume function call will change a temp
		else if(stmt instanceof IRCallStmt) {
			return false;
		}
		return true;
	}

	private void getExprTemps(Set<String> temps, IRExpr expr){
		if(expr instanceof IRBinOp) {
			getExprTemps(temps, ((IRBinOp) expr).left());
			getExprTemps(temps, ((IRBinOp) expr).right());
		}
		else if(expr instanceof IRCall) {
			getExprTemps(temps, ((IRCall) expr).target());
			for(IRExpr arg : ((IRCall) expr).args()) 
				getExprTemps(temps, arg);
		}
		else if(expr instanceof IRTemp) {
			temps.add(((IRTemp) expr).name());
		}
		else if(expr instanceof IRMem) {
			getExprTemps(temps, ((IRMem) expr).expr());
		}
	}

	private String getFreshTemp() {
		return "_t" + FreshTempGenerator.getFreshTemp();
	}
}
