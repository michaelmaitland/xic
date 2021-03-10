package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.util.SExpPrinter;

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
	
}
