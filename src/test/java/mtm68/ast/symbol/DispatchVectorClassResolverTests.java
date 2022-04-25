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

public class DispatchVectorClassResolverTests {
	
	@Test
	public void emptyClassNoSuperclass() {
		ClassDefn c = cDefn("A");
		DispatchVectorClassResolver dvcr = getResolver(c);

		assertTrue(dvcr.getMethods("A").isEmpty());
	}
	
	@Test
	public void emptyClassSuperclassEmpty() {
		ClassDefn c1 = cDefn("A");
		ClassDefn c2 = cDefnExt("B", "A"); 
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);

		assertTrue(dvcr.getMethods("A").isEmpty());
		assertTrue(dvcr.getMethods("B").isEmpty());
	}
	
	@Test
	public void emptyClassSuperclassDefines() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);

		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(1, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(1, BMethods.size());
		assertEquals("A", AMethods.get("f"));
	}
	
	@Test
	public void emptyClassSuperSuperDefines() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A");
		ClassDefn c3 = cDefnExt("C", "B");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2, c3);

		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(1, BMethods.size());
		assertEquals("A", BMethods.get("f"));
		
		Map<String, String> CMethods = dvcr.getMethods("B");
		assertEquals(1, CMethods.size());
		assertEquals("A", CMethods.get("f"));
	}
	
	@Test
	public void singleMethodNoSuper() {
		ClassDefn c1 = cDefn("A", "f");
		DispatchVectorClassResolver dvcr = getResolver(c1);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(1, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		
	}
	
	@Test
	public void multipleMethodNoSuper() {
		ClassDefn c1 = cDefn("A", "f", "g");
		DispatchVectorClassResolver dvcr = getResolver(c1);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(2, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		assertEquals("A", AMethods.get("g"));

	}
	
	@Test
	public void singleMethodSuperDefinesToo() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A", "f");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(1, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(1, BMethods.size());
		assertEquals("B", BMethods.get("f"));
	}
	
	@Test
	public void singleMethodSuperDefinesOther() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefnExt("B", "A", "g");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(1, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(2, BMethods.size());
		assertEquals("A", BMethods.get("f"));
		assertEquals("B", BMethods.get("g"));
	}
	
	@Test
	public void multipleMethodSuperDefinesAll() {
		ClassDefn c1 = cDefn("A", "f", "g");
		ClassDefn c2 = cDefnExt("B", "A", "f", "g");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(2, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		assertEquals("A", AMethods.get("g"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(2, BMethods.size());
		assertEquals("B", BMethods.get("f"));
		assertEquals("B", BMethods.get("g"));
		
	}
	
	@Test
	public void multipleMethodSuperDefinesSome() {
		ClassDefn c1 = cDefn("A", "f", "g");
		ClassDefn c2 = cDefnExt("B", "A", "g", "h");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(2, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		assertEquals("A", AMethods.get("g"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(3, BMethods.size());
		assertEquals("A", BMethods.get("f"));
		assertEquals("B", BMethods.get("g"));
		assertEquals("B", BMethods.get("h"));
	}
	
	@Test
	public void nonRelatedClassesFuncDisjoint1() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefn("B", "g");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(1, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(1, BMethods.size());
		assertEquals("B", BMethods.get("g"));
	}
	
	@Test
	public void nonRelatedClassesFuncDisjoint2() {
		ClassDefn c1 = cDefn("A", "f");
		ClassDefn c2 = cDefn("B", "f");
		DispatchVectorClassResolver dvcr = getResolver(c1, c2);
		
		Map<String, String> AMethods = dvcr.getMethods("A");
		assertEquals(1, AMethods.size());
		assertEquals("A", AMethods.get("f"));
		
		Map<String, String> BMethods = dvcr.getMethods("B");
		assertEquals(1, BMethods.size());
		assertEquals("B", BMethods.get("f"));
	}
	
	private DispatchVectorClassResolver getResolver(ClassDefn...classes) {
		Program prog = new Program(null, new ProgramBody(null, Arrays.asList(classes)));
		SymbolCollector symCollector = new SymbolCollector();
		SymbolTable symTable = symCollector.visit(prog);
		ProgramSymbols progSyms = symTable.toProgSymbols();
		return new DispatchVectorClassResolver(progSyms);
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
