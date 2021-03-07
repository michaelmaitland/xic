package mtm68.ast.nodes.stmts;

public abstract class Assign extends Statement {

	// single decl - LHS + None (RHS)
	// single assign - LHS + RHS
	// multiple assign - List<LHS + wildcard> + fexp
	//
	// LHS -> id | arrayIdx | ty_decl 
	// RHS -> Optional exp 
}
