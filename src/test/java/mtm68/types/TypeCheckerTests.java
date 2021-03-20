package mtm68.types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import mtm68.ast.nodes.stmts.Block;
import mtm68.ast.types.Result;
import mtm68.util.ArrayUtils;
import mtm68.visit.TypeChecker;

public class TypeCheckerTests {
	
	@Test
	void emptyBlock() {
		Block block = new Block(ArrayUtils.empty());
		TypeChecker tc = new TypeChecker();
		Block newBlock = block.accept(tc);
		
		assertEquals(Result.UNIT, newBlock.getResult());
	}

}
