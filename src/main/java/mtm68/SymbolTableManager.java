package mtm68;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import java_cup.runtime.ComplexSymbolFactory;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.Use;
import mtm68.exception.BaseError;
import mtm68.lexer.Lexer;
import mtm68.parser.ParseResult;
import mtm68.parser.Parser;
import mtm68.util.ErrorUtils;

public class SymbolTableManager {
	private Map<String, Map<String, FunctionDecl>> useIdToSymTable;
	private Path libPath;
	
	public SymbolTableManager(Path libPath) {
		useIdToSymTable = new HashMap<>();
		this.libPath = libPath;
	}
	
	public Map<String, FunctionDecl> mergeSymbolTables(Program prog){
		Map<String, FunctionDecl> mergedTable = new HashMap<>();
		for(Use use : prog.getUseStmts()) {
			if(!useIdToSymTable.containsKey(use.getId())) {
				try {
					generateSymbolTableFromLib(use);
				} catch (FileNotFoundException e) {
					System.err.println("Not able to find " + use.getId() +".ixi in library location " + libPath.toString());
				}
			}
			Map<String, FunctionDecl> curMap = useIdToSymTable.get(use.getId());
			for(String f : curMap.keySet()) {
				if(mergedTable.containsKey(f)) {
					if(!mergedTable.get(f).equals(curMap.get(f))) 
						System.err.println("Multiple interface mismatched function declaration for " + curMap.get(f));
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
		//TODO add typecheck
		if(parseResult.isValidAST()) {
			Interface root = (Interface)parseResult.getNode().get();
			generateSymbolTableFromAST(use.getId(), root);
		}
	}
	
	public void generateSymbolTableFromAST(String useId, Interface root) {
		Map<String, FunctionDecl> symTable = new HashMap<>();
		
		for(FunctionDecl decl : root.getFunctionDecls())
			symTable.put(decl.getId(), decl);
		
		useIdToSymTable.put(useId, symTable);
	}
}
