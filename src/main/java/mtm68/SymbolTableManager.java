package mtm68;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.types.ContextType;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.Use;
import mtm68.exception.SemanticException;
import mtm68.lexer.Lexer;
import mtm68.parser.ParseResult;
import mtm68.parser.Parser;
import mtm68.util.ErrorUtils;
import mtm68.visit.TypeChecker;

public class SymbolTableManager {
	private Map<String, Map<String, ContextType>> useIdToSymTable;
	private Path libPath;
	
	public SymbolTableManager(Path libPath) {
		useIdToSymTable = new HashMap<>();
		this.libPath = libPath;
	}
	
	public Map<String, ContextType> mergeSymbolTables(Program prog) throws SemanticException, FileNotFoundException{
		Map<String, ContextType> mergedTable = new HashMap<>();
		for(Use use : prog.getUseStmts()) {
			if(!useIdToSymTable.containsKey(use.getId())) {
					generateSymbolTableFromLib(use);
			}
			Map<String, ContextType> curMap = useIdToSymTable.get(use.getId());
			for(String f : curMap.keySet()) {
				if(mergedTable.containsKey(f)) {
					if(!mergedTable.get(f).equals(curMap.get(f))) 
						throw new SemanticException("Multiple interface mismatched function declaration for " + curMap.get(f));
				}
			}
			mergedTable.putAll(useIdToSymTable.get(use.getId()));
		}
		return mergedTable;
	}

	private void generateSymbolTableFromLib(Use use) throws FileNotFoundException {
		String filename = use.getId() + ".ixi";
		Lexer lexer = new Lexer(new FileReader(libPath.resolve(filename).toString()));
		Parser parser = new Parser(lexer, new ComplexSymbolFactory());
		
		ParseResult parseResult = new ParseResult(parser);
		ErrorUtils.printErrors(parseResult, filename);		
		
		if(parseResult.isValidAST()) {
			Interface root = (Interface)parseResult.getNode().get();
			TypeChecker typeChecker = new TypeChecker();
			root.accept(typeChecker);
			generateSymbolTableFromAST(use.getId(), root);
		}
	}
	
	public void generateSymbolTableFromAST(String useId, Interface root) {
		Map<String, ContextType> symTable = new HashMap<>();
		
		for(FunctionDecl decl : root.getFunctionDecls())
			symTable.put(decl.getId(), new ContextType(decl.getArgs(), decl.getReturnTypes()));
		
		useIdToSymTable.put(useId, symTable);
	}
}
