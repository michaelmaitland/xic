package mtm68.ast.symbol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import mtm68.ast.nodes.ClassBody;
import mtm68.ast.nodes.ClassDefn;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.FunctionDefn;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.ProgramBody;
import mtm68.ast.nodes.stmts.Block;
import mtm68.util.ArrayUtils;
import mtm68.visit.SymbolCollector;

public class DispatchVectorIndexResolverTests {
	
	@Test
	public void emptyClassNoSuperclass() {
		ClassDefn c = cDefn("A");
		DispatchVectorIndexResolver dvir = getResolver(c);

		assertTrue(dvir.getIndicies("A").isEmpty());
	}
	
	@Test
	public void emptyClassSuperclassEmpty() {
		ClassDefn c1 = cDefn("A");
		ClassDefn c2 = cDefnExt("B", "A"); 
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);

		assertTrue(dvir.getIndicies("A").isEmpty());
		assertTrue(dvir.getIndicies("B").isEmpty());
	}
	
	@Test
	public void emptyClassSuperclassDefines() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);

		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(1, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(1, BIndicies.size());
		assertEquals(0, AIndicies.get("f"));
	}
	
	@Test
	public void emptyClassSuperSuperDefines() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A");
		ClassDefn c3 = cDefnExt("C", "B");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2, c3);

		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(1, BIndicies.size());
		assertEquals(0, BIndicies.get("f"));
		
		Map<String, Integer> CIndicies = dvir.getIndicies("B");
		assertEquals(1, CIndicies.size());
		assertEquals(0, CIndicies.get("f"));
	}
	
	@Test
	public void singleMethodNoSuper() {
		ClassDefn c1 = cDefn("A", "f");
		DispatchVectorIndexResolver dvir = getResolver(c1);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(1, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		
	}
	
	@Test
	public void multipleMethodNoSuper() {
		ClassDefn c1 = cDefn("A", "f", "g");
		DispatchVectorIndexResolver dvir = getResolver(c1);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(2, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		assertEquals(1, AIndicies.get("g"));

	}
	
	@Test
	public void singleMethodSuperDefinesToo() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A", "f");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(1, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(1, BIndicies.size());
		assertEquals(0, BIndicies.get("f"));
	}
	
	@Test
	public void singleMethodSuperDefinesOther() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A", "g");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(1, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(2, BIndicies.size());
		assertEquals(0, BIndicies.get("f"));
		assertEquals(1, BIndicies.get("g"));
	}
	
	@Test
	public void multipleMethodSuperDefinesAll() {
		ClassDefn c1 = cDefn("A", "f", "g");
		ClassDefn c2 = cDefnExt("B", "A", "f", "g");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(2, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		assertEquals(1, AIndicies.get("g"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(2, BIndicies.size());
		assertEquals(0, BIndicies.get("f"));
		assertEquals(1, BIndicies.get("g"));
		
	}
	
	@Test
	public void multipleMethodSuperDefinesSome() {
		ClassDefn c1 = cDefn("A", "f", "g");
		ClassDefn c2 = cDefnExt("B", "A", "g", "h");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(2, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		assertEquals(1, AIndicies.get("g"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(3, BIndicies.size());
		assertEquals(0, BIndicies.get("f"));
		assertEquals(1, BIndicies.get("g"));
		assertEquals(2, BIndicies.get("h"));
	}
	
	@Test
	public void nonRelatedClassesFuncDisjoint1() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefn("B", "g");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(1, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(1, BIndicies.size());
		assertEquals(0, BIndicies.get("g"));
	}
	
	@Test
	public void nonRelatedClassesFuncDisjoint2() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefn("B", "f");
		DispatchVectorIndexResolver dvir = getResolver(c1, c2);
		
		Map<String, Integer> AIndicies = dvir.getIndicies("A");
		assertEquals(1, AIndicies.size());
		assertEquals(0, AIndicies.get("f"));
		
		Map<String, Integer> BIndicies = dvir.getIndicies("B");
		assertEquals(1, BIndicies.size());
		assertEquals(0, BIndicies.get("f"));
	}
	
	private DispatchVectorIndexResolver getResolver(ClassDefn...classes) {
		Program prog = new Program(null, new ProgramBody(null, Arrays.asList(classes)));
		SymbolCollector symCollector = new SymbolCollector();
		SymbolTable symTable = symCollector.visit(prog);
		ProgramSymbols progSyms = symTable.toProgSymbols();
		return new DispatchVectorIndexResolver(progSyms);
	}
	
	private List<FunctionDefn> funcs(String... funcs) {
		List<FunctionDecl> decls = ArrayUtils.empty();
		for(String func : funcs) {
			FunctionDecl decl = new FunctionDecl(func, null, null);
			decl.setIsMethod(true);
			decls.add(decl);
		}
		
		return decls.stream()
			 	    .map(decl -> new FunctionDefn(decl, new Block(ArrayUtils.empty())))
			 	    .collect(Collectors.toList());
	}
	
	private ClassDefn cDefn(String className, String... methods) {
		return new ClassDefn(className, new ClassBody(funcs(methods), null));
	}
	
	private ClassDefn cDefnExt(String className, String superClass, String... methods) {
		return new ClassDefn(className, superClass, new ClassBody(funcs(methods), null));
	}
}
