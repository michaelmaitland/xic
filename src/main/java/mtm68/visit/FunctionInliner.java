package mtm68.visit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Var;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.If;
import mtm68.ast.nodes.stmts.MultipleAssign;
import mtm68.ast.nodes.stmts.ProcedureCall;
import mtm68.ast.nodes.stmts.Return;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.nodes.stmts.SingleAssign;
import mtm68.ast.nodes.stmts.Statement;
import mtm68.ast.nodes.stmts.While;

public class FunctionInliner extends Visitor {
	private final int SIZE_CUTOFF = 10;
	Map<String, FunctionDefn> functionDefns;
	
	public FunctionInliner(List<FunctionDefn> fDefns) {
		Map<String, FunctionDefn> defnMap = new HashMap<>();
		
		for(FunctionDefn fDefn : fDefns) {
			defnMap.put(fDefn.getFunctionDecl().getId(), fDefn);
		}
		
		this.functionDefns = defnMap;
	}
	
	@Override
	public Node leave(Node parent, Node n) {
		return n.functionInline(this);
	}

	public Node transformProcedure(ProcedureCall proc) {
		FExpr fexp = proc.getFexp();
		if(!canInline(fexp.getId())) return proc;
			
		Block inlined = transformArgsAndBody(fexp);
		
		for(Statement blockStmt : inlined.getStmts()) {
			if(hasEmbeddedReturns(blockStmt)) return proc;
		}
		
		inlined.setReturnStmt(Optional.empty());
		
		return inlined;
	}
	
	public Node transformSingleAssign(SingleAssign assign) {
		//Check if inlineable
		if(!(assign.getRhs() instanceof FExpr)) return assign;
		FExpr rhs = (FExpr) assign.getRhs();
		if(!canInline(rhs.getId())) return assign;
		
		Node lhs = assign.getLhs();
		Block inlined = transformArgsAndBody(rhs);		
		
		//If we have embedded returns, too complicated for inlining
		for(Statement blockStmt : inlined.getStmts()) {
			if(hasEmbeddedReturns(blockStmt)) return assign;
		}
		
		//Fix return
		List<Statement> stmts = inlined.getStmts();
		Return ret = inlined.getReturnStmt().get();
		SingleAssign retAssign = new SingleAssign(lhs, ret.getRetList().get(0));
		
		stmts.add(retAssign);
		inlined.setReturnStmt(Optional.empty()); 
				
		return inlined;
	}
	
	public Node transformMultiAssign(MultipleAssign assign) {
		FExpr rhs = assign.getRhs();
		if(!canInline(rhs.getId())) return assign;
		
		List<Optional<SimpleDecl>> optDecls = assign.getDecls();
		
		Block inlined = transformArgsAndBody(rhs);
		
		List<Statement> stmts = inlined.getStmts();
		
		//If we have embedded returns, too complicated for inlining
		for(Statement blockStmt : stmts) {
			if(hasEmbeddedReturns(blockStmt)) return assign;
		}
		
		//Fix returns
		Return ret = inlined.getReturnStmt().get();
		List<Expr> retExprs = ret.getRetList();
		
		for(int i = 0; i < optDecls.size(); i++) {
			if(optDecls.get(i).isPresent()) {
				SimpleDecl decl = optDecls.get(i).get();
				
				SingleAssign retAssign = new SingleAssign(decl, retExprs.get(i));
				stmts.add(retAssign);
			}
		}
		
		inlined.setReturnStmt(Optional.empty());		
		return inlined;
	}
	
	private boolean hasEmbeddedReturns(Statement stmt) {
		if(stmt instanceof Block) {
			Block block = (Block) stmt;
			
			if(block.getReturnStmt().isPresent()) 
				return true;
			
			for(Statement blockStmt : block.getStmts())
				if(hasEmbeddedReturns(blockStmt)) return true;
		}
		else if(stmt instanceof If) {
			If subIf = (If) stmt;
			if(hasEmbeddedReturns(subIf.getIfBranch()) || 
					subIf.getElseBranch().isPresent() && hasEmbeddedReturns(subIf.getElseBranch().get())) {
				return true;
			}
		}
		else if(stmt instanceof While) {
			While subWhile = (While) stmt;
			return hasEmbeddedReturns(subWhile.getBody());
		}
		return false;
	}
	
	public Block transformArgsAndBody(FExpr fexp) {
		FunctionDefn fDefn = functionDefns.get(fexp.getId());
		VariableRenamer vr = new VariableRenamer();
		
		fDefn = fDefn.accept(vr);
		
		List<Statement> stmts = new ArrayList<>();
		
		List<Expr> passedArgs = fexp.getArgs();
		List<SimpleDecl> decls = fDefn.getFunctionDecl().getArgs();
		for(int i = 0; i < decls.size(); i++) {
			Var argVar = new Var(decls.get(i).getId());
			SingleAssign argAssign = new SingleAssign(argVar, passedArgs.get(i));
			stmts.add(argAssign);
		}
		
		stmts.addAll(fDefn.getBody().getStmts());
		
		Block block = new Block(stmts);
		block.setReturnStmt(fDefn.getBody().getReturnStmt()); 
		
		return block;
	}
	
	public boolean canInline(String f) {
		if(!functionDefns.containsKey(f)) return false;
		FunctionDefn fDefn = functionDefns.get(f);
		return fDefn.getBody().getStmts().size() < SIZE_CUTOFF;
	}
}
