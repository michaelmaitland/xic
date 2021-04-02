package mtm68.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCall;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Type;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class FExpr extends Expr {
	
	private String id;
	private List<Expr> args;

	public FExpr(String id, List<Expr> args) {
		this.id = id;
		this.args = args;
	}

	public String getId() {
		return id;
	}
	
	public List<Expr> getArgs() {
		return args;
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

	@Override
	public Node visitChildren(Visitor v) {
		List<Expr> newArgs = acceptList(this.args, v);
		if(newArgs != args) {
			FExpr newFExpr = copy();
			newFExpr.args = newArgs;
			return newFExpr;
		} else {
			return this;
		}
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		Type type = tc.checkFunctionCall(this);

		FExpr exp = copy();
		exp.type = type;

		return exp;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		String sym = cv.getFuncSymbol(this);
		IRName name = new IRName(sym);
		List<IRExpr> irArgs = args.stream()
								.map(Expr::getIRExpr)
								.collect(Collectors.toList());

		IRCall call = new IRCall(name, irArgs);

		return copyAndSetIRExpr(call);
	}
}
