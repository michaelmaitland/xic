package mtm68;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import mtm68.ast.nodes.ClassDecl;
import mtm68.ast.nodes.FunctionDecl;
import mtm68.ast.nodes.Interface;
import mtm68.ast.nodes.Program;
import mtm68.ast.nodes.Use;
import mtm68.ast.types.SymbolTable;
import mtm68.exception.SemanticException;
import mtm68.lexer.FileTypeLexer;
import mtm68.lexer.Lexer;
import mtm68.lexer.TokenFactory;
import mtm68.parser.ParseResult;
import mtm68.parser.Parser;
import mtm68.util.ErrorUtils;

public class SymbolTableManager {
	private Map<String, SymbolTable> useIdToSymTable;
	private Path libPath;
	
	public SymbolTableManager(Path libPath) {
		useIdToSymTable = new HashMap<>();
		this.libPath = libPath;
	}
	
	public SymbolTable mergeSymbolTables(Program prog) throws SemanticException{
		SymbolTable mergedTable = new SymbolTable();
		for(Use use : prog.getUseStmts()) {
			if(!useIdToSymTable.containsKey(use.getId())) {
				try {
					generateSymbolTableFromLib(use);
				}
				catch(FileNotFoundException e) {
					throw new SemanticException(use, use.getId() + ".ixi not found in library " + libPath);
				}
			}
			SymbolTable curTable = useIdToSymTable.get(use.getId());
			
			mergeFunctionDecls(mergedTable, curTable, use);
			mergeClassDecls(mergedTable, curTable, use);
		}
		return mergedTable;
	}
	
	private void mergeFunctionDecls(SymbolTable mergeTo, SymbolTable toMerge, Use use) throws SemanticException {
		Map<String, FunctionDecl> toMergeFuncs = toMerge.getFunctionDecls();
		Map<String, FunctionDecl> mergedFuncs = mergeTo.getFunctionDecls();
		for(String f : toMergeFuncs.keySet()) {
			if(mergedFuncs.containsKey(f)) {
				if(!mergedFuncs.get(f).equals(toMergeFuncs.get(f))) 
					throw new SemanticException(use, "Multiple interface mismatched function declaration for " + toMergeFuncs.get(f));
				}
			}
			mergedFuncs.putAll(useIdToSymTable.get(use.getId()).getFunctionDecls());
	}
	
	
	private void mergeClassDecls(SymbolTable mergeTo, SymbolTable toMerge, Use use) throws SemanticException {
		Map<String, ClassDecl> toMergeClasses = toMerge.getClassDecls();
		Map<String, ClassDecl> mergedClasses = mergeTo.getClassDecls();
		for(String c : toMergeClasses.keySet()) {
			if(mergedClasses.containsKey(c)) {
				if(!mergedClasses.get(c).equals(toMergeClasses.get(c))) 
					throw new SemanticException(use, "Multiple interface mismatched class declaration for " + toMergeClasses.get(c));
				}
			}
			mergedClasses.putAll(useIdToSymTable.get(use.getId()).getClassDecls());
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
		SymbolTable symTable = new SymbolTable(root);
		useIdToSymTable.put(useId, symTable);
	}
}
