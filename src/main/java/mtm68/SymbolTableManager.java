package mtm68;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.Use;
import mtm68.exception.SemanticException;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.lexer.TokenFactory;
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
	
	public Map<String, FunctionDecl> mergeSymbolTables(Program prog) throws SemanticException{
		Map<String, FunctionDecl> mergedTable = new HashMap<>();
		for(Use use : prog.getUseStmts()) {
			if(!useIdToSymTable.containsKey(use.getId())) {
				try {
					generateSymbolTableFromLib(use);
				}
				catch(FileNotFoundException e) {
					throw new SemanticException(use, use.getId() + ".ixi not found in library " + libPath);
				}
			}
			Map<String, FunctionDecl> curMap = useIdToSymTable.get(use.getId());
			for(String f : curMap.keySet()) {
				if(mergedTable.containsKey(f)) {
					if(!mergedTable.get(f).equals(curMap.get(f))) 
						throw new SemanticException(use, "Multiple interface mismatched function declaration for " + curMap.get(f));
				}
			}
			mergedTable.putAll(useIdToSymTable.get(use.getId()));
		}
		return mergedTable;
	}

	private void generateSymbolTableFromLib(Use use) throws FileNotFoundException, SemanticException {
		String filename = use.getId() + ".ixi";
		
		TokenFactory tokenFactory = new TokenFactory();
		Lexer lexx = new FileTypeLexer(filename, libPath, FileType.parseFileType(filename), tokenFactory);
		Parser parser = new Parser(lexx, tokenFactory);
		
		ParseResult parseResult = new ParseResult(parser);
		ErrorUtils.printErrors(parseResult, filename);		
		
		if(parseResult.isValidAST()) {
			Interface root = (Interface)parseResult.getNode().get();
			generateSymbolTableFromAST(use.getId(), root);
		}
		else {
			throw new SemanticException(use, "Parse error in library interface file");
		}
	}
	
	public void generateSymbolTableFromAST(String useId, Interface root) {
		Map<String, FunctionDecl> symTable = new HashMap<>();
		
		for(FunctionDecl decl : root.getFunctionDecls())
			symTable.put(decl.getId(), decl);
		
		useIdToSymTable.put(useId, symTable);
	}
}
