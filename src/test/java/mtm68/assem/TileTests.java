package mtm68.assem;

import static mtm68.ir.IRTestUtils.*;
import static mtm68.util.TestUtils.*;

import org.junit.jupiter.api.Test;

import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.ir.visit.Tiler;
import mtm68.assem.op.LeaAssem;

public class TileTests {

	@Test
	void tileAdd() {
		IRNode plus = op(OpType.ADD, temp("t1"), temp("t2"));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(plus));
		
		System.out.println(tiled); 
	}
	
	@Test
	void tileJump() {
		IRNode jump = jump("f");
		JumpAssem tiled = assertInstanceOfAndReturn(JumpAssem.class, tile(jump));
		
		System.out.println(tiled); 
	}
	
	@Test
	void tileMem() {
		IRNode mem = mem(temp("t1"));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(mem));
		
		System.out.println(tiled); 
	}
	
	@Test
	void tileMoveRegToReg() {
		IRNode move = move("t1", "t2");
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
		System.out.println(tiled); 
	}
	
	@Test
	void tileMoveMemToReg() {
		IRNode move = move("t1", mem(temp("t2")));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
		System.out.println(tiled); 
	}
	
	@Test
	void tileMoveRegToMem() {
		IRNode move = move(mem(temp("t2")), temp("t1"));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
		System.out.println(tiled); 
	}
	
	@Test
	void tileMoveRegTtoMem2() {
		IRNode move = move(mem(op(OpType.ADD, temp("t1"), temp("t2"))), op(OpType.ADD, temp("t3"), temp("t4")));
		SeqAssem tiled = assertInstanceOfAndReturn(SeqAssem.class, tile(move));
		
		System.out.println(tiled); 
	}
	
	private Assem tile(IRNode node) {
		Tiler tiler = new Tiler(new IRNodeFactory_c());
		return tiler.visit(node).getAssem();
	}
	
}
