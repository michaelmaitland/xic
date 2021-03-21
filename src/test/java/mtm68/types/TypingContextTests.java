package mtm68.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.types.Type;
import mtm68.ast.types.Types;
import mtm68.ast.types.TypingContext;
import mtm68.util.ArrayUtils;

public class TypingContextTests {
	TypingContext context = new TypingContext();

	@Test
	void testVarBindings() {
		context.addIdBinding("x", Types.BOOL);
		context.addIdBinding("y", Types.INT);
		context.addIdBinding("a", Types.ARRAY(Types.INT));
		
		assertTrue(context.isDefined("x") && context.isDefined("y") && context.isDefined("a"));
		assertEquals(Types.BOOL, context.getIdType("x"));
		assertEquals(Types.INT, context.getIdType("y"));
		assertEquals(Types.ARRAY(Types.INT), context.getIdType("a"));

		context.enterScope();		
		assertTrue(context.isDefined("x") && context.isDefined("y") && context.isDefined("a"));
		
		context.leaveScope();
		assertTrue(context.isDefined("x") && context.isDefined("y") && context.isDefined("a"));
	}
	
	@Test
	void testFuncDecl() {
		List<SimpleDecl> decls = ArrayUtils.empty();
		context.addFuncDecl("f", decls, ArrayUtils.singleton(Types.BOOL));
		
		assertTrue(context.isFunctionDecl("f"));
		assertTrue(context.takesInUnit("f"));
		assertTrue(!context.getReturnTypes("f").isEmpty());
	}
	
	@Test
	void testFunctionScope() {
		List<SimpleDecl> decls = ArrayUtils.elems(new SimpleDecl("x", Types.INT), new SimpleDecl("y", Types.BOOL));
		List<Type> returnTypes = ArrayUtils.singleton(Types.BOOL);
		context.addFuncDecl("f", decls, returnTypes);
		
		assertTrue(context.isFunctionDecl("f"));
		assertEquals(returnTypes, context.getReturnTypes("f"));
		assertEquals(decls.size(), context.getArgTypes("f").size());
		
		//Enter function body
		context.enterScope();
		context.addFuncBindings(decls, returnTypes);
		
		assertTrue(context.isDefined("x") && context.isDefined("y"));
		assertEquals(Types.INT, context.getIdType("x"));
		assertEquals(Types.BOOL, context.getIdType("y"));
		assertEquals(returnTypes, context.getReturnTypeInScope());
		
		//Enter inner scope (i.e. if statement)
		context.enterScope();
		assertTrue(context.isDefined("x") && context.isDefined("y"));
		assertEquals(returnTypes, context.getReturnTypeInScope());

		//Leave inner scope and function body
		context.leaveScope();
		context.leaveScope();

		assertTrue(!context.isDefined("x") && !context.isDefined("y"));
		//Test for RHO
		assertTrue(!context.isDefined("!!!"));
	}
	
	@Test
	void testLocalVariableOverride() {
		context.addIdBinding("x", Types.BOOL);

		assertEquals(Types.BOOL, context.getIdType("x"));

		context.enterScope();
		context.addIdBinding("x", Types.INT);
		
		assertEquals(Types.INT, context.getIdType("x"));

		context.leaveScope();

		assertEquals(Types.BOOL, context.getIdType("x"));
	}
}
