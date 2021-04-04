package mtm68.ast.nodes;

import java.util.List;
import java.util.stream.Collectors;

import edu.cornell.cs.cs4120.ir.IRESeq;
import edu.cornell.cs.cs4120.ir.IRExpr;
import edu.cornell.cs.cs4120.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import mtm68.ast.types.Types;
import mtm68.util.ArrayUtils;
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
	public Node convertToIR(NodeToIRNodeConverter cv, IRNodeFactory irFactory) {
		List<IRExpr> items = ArrayUtils.stringToCharList(value)
							.stream()
							.map(ch -> irFactory.IRConst(ch))
							.collect(Collectors.toList());
		
		IRESeq eseq = cv.allocateAndInitArray(items);
		return copyAndSetIRExpr(eseq);
	}
}
