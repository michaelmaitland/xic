package mtm68.opt;

import static mtm68.util.ArrayUtils.*;
import static mtm68.util.NodeTestUtil.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import mtm68.ast.nodes.stmts.Statement;
import mtm68.ast.types.Types;

public class RenameVarsTests {
	@Test
	void testBasicRename(){
		List<Statement> stmts = elems(
				sAssign(simDecl("x", Types.INT), intLit(1L)));
	}
	
	
}
