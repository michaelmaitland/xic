package mtm68.ast.nodes;

import java.util.List;

import edu.cornell.cs.cs4120.ir.IRConst;
import edu.cornell.cs.cs4120.ir.IRStmt;
import edu.cornell.cs.cs4120.ir.IRTemp;
import edu.cornell.cs.cs4120.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.util.StringUtils;
import mtm68.visit.NodeToIRNodeConverter;
import mtm68.visit.TypeChecker;
import mtm68.visit.Visitor;

public class StringLiteral extends Literal<String>{

	public StringLiteral(String value) {
		super(value);
		setType(Types.ARRAY(Types.INT));
	}
	
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}

	@Override
	public void prettyPrint(SExpPrinter p) {
		p.printAtom("\"" + StringUtils.preserveNewlines(value) + "\""); 
	}

	@Override
	public Node visitChildren(Visitor v) {
		return this;
	}

	@Override
	public Node typeCheck(TypeChecker tc) {
		return this;
	}

	@Override
	public Node convertToIR(NodeToIRNodeConverter cv) {
		String tmp = cv.newTemp();
		int len = value.length();
		List<IRStmt> seq = allocateArray(new IRTemp(tmp, new IRConst(len)));
		
		for(int i=0; i <value.length(); i++) {
			// set length
			if(i == 0) {
				seq.add(new IRMove(new  IRMem(new IRTemp(t)), new IRConst(len)));
			} else {
				seq.add(new IRMove(new IRMem(
						new IRBinOp(OpType.ADD, new IRTemp(t), new IRConst(8 * i)))))
			}
		}
		
		return new IRESeq(new IRSeq(seq), new IRTemp(t));
		
	}
}
