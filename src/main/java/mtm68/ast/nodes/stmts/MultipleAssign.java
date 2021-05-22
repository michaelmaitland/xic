package mtm68.ast.nodes.stmts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.ast.types.Type;
import mtm68.ast.types.Types;
import mtm68.util.ArrayUtils;
import mtm68.visit.FunctionInliner;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class MultipleAssign extends Assign {
	
	private List<Optional<SimpleDecl>> decls;
	private FExpr rhs;
	
	public MultipleAssign(List<Optional<SimpleDecl>> decls, FExpr rhs) {
		this.decls = decls;
		this.rhs = rhs;
	}

	public List<Optional<SimpleDecl>> getDecls() {
		return decls;
	}
	
	public FExpr getRhs() {
		return rhs;
	}

	@Override
	public String toString() {
		return "MultipleAssign [decls=" + decls + ", rhs=" + rhs + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("=");
		p.startList();
		for(Optional<SimpleDecl> optDecl : decls)
			if(optDecl.isPresent()) optDecl.get().prettyPrint(p);
			else p. printAtom("_ ");
		p.endList();
		rhs.prettyPrint(p);
		p.endList();
	}
	
	@Override
	public Node visitChildren(Visitor v) {
		// Check RHS before LHS so we cant use vars declared on LHS on RHS
		FExpr newRhs = rhs.accept(v);

		if(decls == null) {
			return this;
		}
			
		List<Optional<SimpleDecl>> newDecls = decls;
		List<Optional<SimpleDecl>> vl = new ArrayList<>(decls.size());
		
		for(Optional<SimpleDecl> d : decls) {
			if(!d.isPresent()) {
				vl.add(d);
				continue;
			} else {
				SimpleDecl s = d.get();
				SimpleDecl newS = s.accept(v); 
				if(newS != s) {
					newDecls = vl;
				}
				vl.add(Optional.of(newS));
			}
		}
			
		if(newRhs != rhs || newDecls != decls) {
			MultipleAssign multi = copy();
			multi.decls = newDecls;
			multi.rhs = newRhs;

			return multi;
		} 
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type funcType = rhs.getType();
		List<Type> returnTypes = Types.toList(funcType);

		List<Type> declTypes = decls.stream()
				.map(d -> d.map(SimpleDecl::getType))
				.map(d -> d.orElse(Types.UNIT))
				.collect(Collectors.toList());
		
		tc.checkSubtypes(this, returnTypes, declTypes);
		
		MultipleAssign assign = copy();
		assign.result = Result.UNIT;
		return assign;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory inf) {
		
		String sym = cv.getFuncSymbol(rhs);
		IRName name = inf.IRName(sym);
		List<IRExpr> irArgs = rhs.getArgs().stream()
								.map(Expr::getIRExpr)
								.collect(Collectors.toList());
		
		int numRets = decls.size();
		IRCallStmt call = inf.IRCallStmt(name, numRets, irArgs);	

		List<IRStmt> stmts = ArrayUtils.empty();
		stmts.add(call);
		
		for(int i=0; i < decls.size(); i++) {
			String retValue = cv.retVal(i);
			decls.get(i).ifPresent(decl -> {
				IRMove mov = inf.IRMove(
						inf.IRTemp(cv.newTemp(decl.getId())),
						inf.IRTemp(retValue)
					);
				stmts.add(mov);
			});
		}

		return copyAndSetIRStmt(inf.IRSeq(stmts));
	}
	
	@Override
	public Node functionInline(FunctionInliner fl) {
		return fl.transformMultiAssign(this);
	}
}
