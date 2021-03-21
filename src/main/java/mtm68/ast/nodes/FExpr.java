package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class FExpr extends Expr {
	
	private String id;
	private List<Expr> args;

	public FExpr(String id, List<Expr> args) {
		this.id = id;
		this.args = args;
	}

	@Override
	public String toString() {
		return "FExpr [id=" + id + ", args=" + args + "]";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.startList();
		p.printAtom(id);
		for(Expr arg: args) arg.prettyPrint(p);
		p.endList();
	}
	
	public String getId() {
		return id;
	}
	
	public List<Expr> getArgs() {
		return args;
	}

	@Override
	public Node visitChildren(Visitor v) {
		List<Expr> newArgs = acceptList(this.args, v);
		if(newArgs != args) {
			return new FExpr(id, newArgs);
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkFunctionCall(this);

		FExpr exp = new FExpr(id, args);
		exp.type = type;

		return exp;
	}
	
}
