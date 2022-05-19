package mtm68.types;

import static mtm68.util.ArrayUtils.elems;
import static mtm68.util.ArrayUtils.empty;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.symbol.SymbolTable;
import mtm68.ast.types.Types;
import mtm68.ast.types.TypingContext;
import mtm68.visit.SymbolCollector;
import mtm68.visit.Visitor;

public class SymbolCollectorTests {

	@Test
	void testFunctionCollection() {
		Program prog = new Program(empty(),
				elems(func("f"),
						func("g"),
						func("h")));
		
		TypingContext context = symbolCollect(prog);
		
		assertTrue(context.isDefined("f"));
		assertTrue(context.isFunctionDecl("f"));
		assertTrue(context.isDefined("g"));
		assertTrue(context.isFunctionDecl("g"));
		assertTrue(context.isDefined("h"));
		assertTrue(context.isFunctionDecl("h"));
	}
	
	@Test
	void testDuplicateDecl() {
		Program prog = new Program(empty(),
				elems(func("f"),
						func("f")));
		
		assertSymbolCollectError(prog);
	}
	
	@Test
	void testInterfaceMismatch() {
		SymbolTable initSymTable = new SymbolTable();
		initSymTable.putFunc("f", new FunctionDecl("f", elems(new SimpleDecl("x", Types.INT)), elems(Types.INT)));
		
		//Matching decls
		Program prog = new Program(empty(),
				elems(func("f")));
		
		TypingContext context = symbolCollect(prog, initSymTable);
		
		assertTrue(context.isDefined("f"));
		assertTrue(context.isFunctionDecl("f"));
		
		//Mismatch decl
		prog = new Program(empty(),
				elems(func("f", elems(new SimpleDecl("y", Types.BOOL)))));
		
		assertSymbolCollectError(prog, initSymTable);
	}
	
	private FunctionDefn func(String name, List<SimpleDecl> args) {
		FunctionDecl decl = new FunctionDecl(name, args, elems(Types.INT));
		Block block = new Block(empty());
		return new FunctionDefn(decl, block);
	}
	
	private FunctionDefn func(String name) {
		return func(name, elems(new SimpleDecl("x", Types.INT)));
	}
	
	private TypingContext symbolCollect(Node root) {
		return symbolCollect(root, new SymbolTable());
	}
	
	private TypingContext symbolCollect(Node root, SymbolTable initSymTable) {
		SymbolCollector fc = new SymbolCollector(initSymTable);
		addLocs(root);
		return new TypingContext(fc.visit(root));
	}
	
	private void assertSymbolCollectError(Node root) {
		assertSymbolCollectError(root, new SymbolTable());
	}
	
	private void assertSymbolCollectError(Node root, SymbolTable initSymTable) {
		SymbolCollector fc = new SymbolCollector(initSymTable);
		addLocs(root);
		fc.visit(root);
		assertTrue(fc.hasError(), "Expected collect error but got none");
	}
	
	private void addLocs(Node n) {
		n.accept(new Visitor() {
			@Override
			public Node leave(Node parent, Node n) {
				n.setStartLoc(new Location(0, 0));
				return n;
			}
		});
	}
}
