package mtm68.types;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.util.InternalCompilerError;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.FExpr;
import mtm68.ast.nodes.Node;
import mtm68.util.ArrayUtils;
import mtm68.visit.ThisAugmenter;



public class ThisAugmenterTests {

	//-------------------------------------------------------------------------------- 
	// FExpr
	//-------------------------------------------------------------------------------- 
	
	@Test
	void dummyAugment() {
		FExpr f = new FExpr("f", ArrayUtils.empty());
		FExpr newF = augment(f);
	}

	@Test
	void dummyError() {
		FExpr f = new FExpr("f", ArrayUtils.empty());;
		assertError(f);
	}

	//-------------------------------------------------------------------------------- 
	// FunctionDecl 
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// FunctionDefn 
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// Var
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// SimpleDecl
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// ExtendedDecl
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// FieldAccess
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// MethodCall
	//-------------------------------------------------------------------------------- 
	//-------------------------------------------------------------------------------- 
	// New
	//-------------------------------------------------------------------------------- 


	//-------------------------------------------------------------------------------- 
	// Helper Methods
	//-------------------------------------------------------------------------------- 

	private <N extends Node> N augment(Optional<ClassDefn> currClass, Stack<List<String>> context, N node) {
		try {
			ThisAugmenter ta = new ThisAugmenter(currClass, context);
			node = ta.performAugment(node);
			return node;
		} catch (InternalCompilerError e) {
			assertTrue(false, "Expected no errors but got " + e.getMessage());
		}

		return node;
	}
	
	private <N extends Node> N augment(Optional<ClassDefn> currClass, N node) {
		return augment(currClass, new Stack<>(), node);
	}
	
	private <N extends Node> N augment(Stack<List<String>> context, N node) {
		return augment(Optional.empty(), context, node);
	}

	private <N extends Node> N augment(N node) {
		return augment(Optional.empty(), new Stack<>(), node);
	}
	
	private <N extends Node> void assertError(Optional<ClassDefn> currClass, 
			Stack<List<String>> context, N node) {
		ThisAugmenter ta = new ThisAugmenter(currClass, context);
		assertThrows(InternalCompilerError.class, () -> ta.performAugment(node));
	}

	private <N extends Node> void assertError(N node) {
		assertError(Optional.empty(), new Stack<>(), node);
	}
}
