package mtm68.ast.nodes.stmts;

import java.util.Optional;

import mtm68.ast.types.Type;

public interface SingleAssignLHS {
	
	String getName();
	Optional<Type> getType();

}
