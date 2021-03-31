package mtm68.util;

import static org.junit.Assert.assertTrue;

import java_cup.runtime.ComplexSymbolFactory.Location;
import mtm68.ast.nodes.ArrayInit;
import mtm68.ast.nodes.BoolLiteral;
import mtm68.ast.nodes.CharLiteral;
import mtm68.ast.nodes.Expr;
import mtm68.ast.nodes.IntLiteral;
import mtm68.ast.nodes.Node;
import mtm68.ast.nodes.StringLiteral;
import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.nodes.stmts.SimpleDecl;
import mtm68.ast.types.Type;
import mtm68.visit.Visitor;

public class NodeTestUtil {

	public static Expr arbitraryCondition() {
		return new BoolLiteral(true);
	}

	public static IntLiteral intLit(Long value) {
		return new IntLiteral(value);
	}
	
	public static CharLiteral charLit(Character value) {
		return new CharLiteral(value);
	}

	public static BoolLiteral boolLit(boolean value) {
		return new BoolLiteral(value);
	}

	public static StringLiteral stringLit(String value) {
		return new StringLiteral(value);
	}
	
	public static SimpleDecl simDecl(String id, Type type) {
		return new SimpleDecl(id, type);
	}

	public static Block emptyBlock() {
		return new Block(ArrayUtils.empty());
	}

	public static ArrayInit emptyArray() {
		return new ArrayInit(ArrayUtils.empty());
	}
	
	public static ArrayInit arrayWithElems(Expr... elems){
		return new ArrayInit(ArrayUtils.elems(elems));
	}
	
	public static void addLocs(Node n) {
		n.accept(new Visitor() {
			@Override
			public Node leave(Node parent, Node n) {
				n.setStartLoc(new Location(0, 0));
				return n;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> T assertInstanceOfAndReturn(Class<T> clazz, Object obj) {
		assertTrue(obj.getClass() + " is not an instanceof " + clazz , clazz.isAssignableFrom(obj.getClass()));
		return (T) obj;
	}

	public static <T> void assertInstanceOf(Class<T> clazz, Object obj) {
		assertInstanceOfAndReturn(clazz, obj);
	}


}
