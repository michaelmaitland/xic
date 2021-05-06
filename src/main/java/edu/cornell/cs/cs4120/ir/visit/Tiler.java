package edu.cornell.cs.cs4120.ir.visit;

import static mtm68.util.ArrayUtils.*;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRCallStmt;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRFuncDefn;
import edu.cornell.cs.cs4120.ir.IRMove;
import edu.cornell.cs.cs4120.ir.IRName;
import edu.cornell.cs.cs4120.ir.IRNode;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.ir.IRTemp;
import mtm68.assem.Assem;
import mtm68.assem.CallAssem;
import mtm68.assem.LabelAssem;
import mtm68.assem.MoveAssem;
import mtm68.assem.PopAssem;
import mtm68.assem.PushAssem;
import mtm68.assem.RetAssem;
import mtm68.assem.SeqAssem;
import mtm68.assem.op.AddAssem;
import mtm68.assem.op.LeaAssem;
import mtm68.assem.op.SubAssem;
import mtm68.assem.operand.FreshRegGenerator;
import mtm68.assem.operand.Imm;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.RealReg.RealRegId;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;
import mtm68.assem.tile.TileCosts;
import mtm68.util.ArrayUtils;
import mtm68.util.Constants;

/**
 * IRVisitor that performs tiling using the optimal tiling algorithm.
 * 
 * @author Scott
 */
public class Tiler extends IRVisitor {

	private boolean afterCallStmt;
	private IRCallStmt callStmt;
	private List<IRMove> moveStmts;
	private int retSpaceOff;

	public Tiler(IRNodeFactory inf) {
		super(inf);
		
		moveStmts = ArrayUtils.empty(); 
	}

	@Override
	protected IRVisitor enter(IRNode parent, IRNode n) {
		if (afterCallStmt && !(isRetMove(n) || isRetMove(parent))) { // MOVE t1 RET0
			afterCallStmt = false;

			// Add in instructions to reset ret space
			List<Assem> moveAssems = moveStmts.stream()
				.map(this::convertRetMov)
				.collect(Collectors.toList());

			callStmt.appendAssems(moveAssems);
			
			int stackOffset = getFunctionStackSize();
			
			callStmt.appendAssems(elems(
					new AddAssem(RealReg.RSP, new Imm(stackOffset))
					));
			
			// So we don't double count the move stmts
			moveStmts.forEach(m -> m.setAssem(null));
			
			// We're done with these
			callStmt = null;
			moveStmts.clear();
		} else if(n instanceof IRFuncDefn) {
			IRFuncDefn func = (IRFuncDefn) n;
			setRetSpaceOff(Constants.WORD_SIZE * (getExtraArgCount(func.numArgs()) + 3));
		}
		return this;
	}
	
	private Assem convertRetMov(IRMove move) {
		
		String srcTemp = ((IRTemp)move.source()).name();
		Integer retVal = Integer.parseInt(srcTemp.replace(Constants.RET_PREFIX, ""));
		
		Src src = null;
		switch(retVal) {
		case 0: 
			src = RealReg.RAX;
			break;
		case 1: 
			src = RealReg.RDX;
			break;
		default: 
			int retSpaceOff = Constants.WORD_SIZE * (retVal - 1);
			src = new Mem(RealReg.RSP, getFunctionStackSize() - retSpaceOff);
			break;
		}
		
		return new MoveAssem(move.target().getResultReg(), src);
	}
	
	private int getFunctionStackSize( ) {
		return Constants.WORD_SIZE * (getExtraArgCount(callStmt) + getExtraRetCount(callStmt) + 2);
	}

	private boolean isCallStmt(IRNode n) {
		return n instanceof IRCallStmt;
	}

	private boolean isRetMove(IRNode n) {
		if (!(n instanceof IRMove))
			return false;

		IRMove mov = (IRMove) n;

		if (!(mov.source() instanceof IRTemp))
			return false;

		IRTemp src = (IRTemp) mov.source();

		return src.name().startsWith(Constants.RET_PREFIX);
	}

	@Override
	protected IRNode leave(IRNode parent, IRNode n, IRNode n_, IRVisitor v_) {
		IRNode tiled = n_.tile(this);

		if (isCallStmt(tiled)) {
			afterCallStmt = true;
			callStmt = (IRCallStmt) tiled;
		} else if(isRetMove(tiled)) {
			moveStmts.add((IRMove)tiled);
		}

		return tiled;
	}

	public IRNode tileCallStmt(IRCallStmt stmt) {
		List<Assem> assems = empty();

		List<IRExpr> argExprs = stmt.args();
		List<Assem> argAssems = argExprs.stream().map(IRNode::getAssem).collect(Collectors.toList());

		List<RealReg> argRegs = RealRegId.getArgRegs();

		// Make space for returns
		int retSpaceSize = Constants.WORD_SIZE * getExtraRetCount(stmt);

		if (retSpaceSize > 0) {
			assems.add(new SubAssem(RealReg.RSP, new Imm(retSpaceSize)));
		}

		// Push ret space pointer
		Reg reg = FreshRegGenerator.getFreshAbstractReg();
		assems.add(new LeaAssem(reg, new Mem(RealReg.RSP, retSpaceSize - Constants.WORD_SIZE)));
		assems.add(new PushAssem(reg));
		
		// 16 bytes in total for ret space pointer
		assems.add(new SubAssem(RealReg.RSP, new Imm(Constants.WORD_SIZE)));

		// Put args 1-6 in their designated registers
		for (int i = 0; i < argRegs.size() && i < argAssems.size(); i++) {
			Assem argAssem = argAssems.get(i);
			assems.add(argAssem);

			Reg src = argExprs.get(i).getResultReg();
			Reg dest = argRegs.get(i);
			assems.add(new MoveAssem(dest, src));
		}

		// Push args 7-n on the stack in reverse order
		for (int i = argAssems.size() - 1; i >= argRegs.size(); i--) {
			Assem argAssem = argAssems.get(i);
			assems.add(argAssem);

			Reg src = argExprs.get(i).getResultReg();
			assems.add(new PushAssem(src));
		}

		String funcName = ((IRName) stmt.target()).name();
		assems.add(new CallAssem(funcName));

		// Since there's only one way to tile a IRCallStmt we can just give it no cost
		return stmt.copyAndSetAssem(new SeqAssem(assems), TileCosts.NO_COST);
	}
	
	private int getExtraArgCount(IRCallStmt stmt) {
		return getExtraArgCount(stmt.args().size());
	}

	private int getExtraArgCount(int numArgs) {
		return Math.max(numArgs - 6, 0);
	}

	private int getExtraRetCount(IRCallStmt stmt) {
		return roundUpToEven(Math.max(stmt.getNumRets() - 2, 0));
	}
	
	private int roundUpToEven(int num) {
		return num % 2 == 0 ? num : num + 1;
	}
	
	/** Offset from rbp */
	public int getRetSpaceOff() {
		return retSpaceOff;
	}
	
	public void setRetSpaceOff(int retSpaceOff) {
		this.retSpaceOff = retSpaceOff;
	}

	/**
	 * Get assembly instructions used as the prologue of a function.
	 * 
	 * @param name
	 * @param numArgs
	 * @param l
	 * @return
	 */
	public SeqAssem getPrologue(String name, int numArgs, int l) {
		// In our stack frame we include a ret space pointer which isn't included
		// when our main function is called. For alignment reasons, we have to treat
		// the main function specially since its stack frame is off by one.
		boolean isMainFunc = name.equals(Constants.MAIN_FUNC);
		
		// If we are allocating an odd number of words in total, we need to add an
		// extra word to ensure 16 byte alignment
		int align = l + getExtraArgCount(numArgs);
		if(!isMainFunc) align += 2; // For ret space pointer
		
		if(align % 2 == 1) l++;
		
		return new SeqAssem(
				new LabelAssem(name),
				new PushAssem(RealReg.RBP),
				new MoveAssem(RealReg.RBP, RealReg.RSP),
				new SubAssem(RealReg.RSP, new Imm(Constants.WORD_SIZE * l))
			);
	}

	/**
	 * Get assembly instructions corresponding to the epilogue of a function.
	 * @return
	 */
	public SeqAssem getEpilogue() {
		return new SeqAssem(
				new MoveAssem(RealReg.RSP, RealReg.RBP),
				new PopAssem(RealReg.RBP),
				new RetAssem()
			);
	}
}
