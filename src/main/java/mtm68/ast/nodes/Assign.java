package mtm68.ast.nodes;

public abstract class Assign extends Statement {

	// single decl - LHS + None (RHS)
	// single assign - LHS + RHS
	// multiple assign - List<LHS + wildcard> + fexp
	//
	// LHS -> id | arrayIdx | id : ty | arrayIdx : ty
	// RHS -> Optional exp 
}
