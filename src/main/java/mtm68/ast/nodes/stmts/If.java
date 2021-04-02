package mtm68.ast.nodes.stmts;

import java.util.Optional;

import edu.cornell.cs.cs4120.ir.IRLabel;
import edu.cornell.cs.cs4120.ir.IRSeq;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.Node;
import mtm68.ast.types.Result;
import mtm68.ast.types.Types;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class If extends Statement {
	
	private Expr condition;
	private Statement ifBranch;
	private Optional<Statement> elseBranch;

	public If(Expr condition, Statement ifBranch) {
		this(condition, ifBranch, Optional.empty());
	}

	public If(Expr condition, Statement ifBranch, Statement elseBranch) {
		this(condition, ifBranch, Optional.of(elseBranch));
	}
	
	public If(Expr condition, Statement ifBranch, Optional<Statement> elseBranch) {
		this.condition = condition;
		this.ifBranch = ifBranch;
		this.elseBranch = elseBranch;
	}

	public Expr getCondition() {
		return condition;
	}

	public Statement getIfBranch() {
		return ifBranch;
	}

	public Optional<Statement> getElseBranch() {
		return elseBranch;
	}

	@Override
	public String toString() {
		return "If [condition=" + condition + ", ifBranch=" + ifBranch + ", elseBranch=" + elseBranch + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom("if");
		condition.prettyPrint(p);
		if(elseBranch.isPresent()) {
			ifBranch.prettyPrint(p);
			elseBranch.get().prettyPrint(p);
		}
		else {
			ifBranch.prettyPrint(p);
		}
		p.endList();
	}

	@Override
	public Node visitChildren(Visitor v) {
		Expr newCondition = condition.accept(v);
		Statement newIfBranch = ifBranch.accept(this, v);
		Optional<Statement> newElseBranch = acceptOptional(this, elseBranch, v);
		
		if(newCondition != condition
				|| newIfBranch != ifBranch
				|| newElseBranch != elseBranch) {
			If stmt = copy();
			stmt.condition = newCondition;
			stmt.ifBranch = newIfBranch;
			stmt.elseBranch = newElseBranch;

			return stmt;
		} 

		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		tc.checkType(condition, Types.BOOL);

		If stmt = copy(); 
		
		Result ifRes = ifBranch.result;
		Result elseRes = elseBranch.isPresent() ? elseBranch.get().getResult() : Result.UNIT;

		stmt.result = Result.leastUpperBound(ifRes, elseRes);

		return stmt;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {

		String trueLabel = cv.getFreshLabel();
		String falseLabel = cv.getFreshLabel();
		
		IRSeq seq = new IRSeq(
					cv.getCtrlFlow(condition, trueLabel, falseLabel),
					new IRLabel(trueLabel),
					ifBranch.getIRStmt(),
					new IRLabel(falseLabel)
					);
		if(elseBranch.isPresent()) {
			seq.stmts().add(elseBranch.get().getIRStmt());
		}

		return copyAndSetIRStmt(seq);
	}
}
