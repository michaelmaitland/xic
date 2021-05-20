package edu.cornell.cs.cs4120.ir;

import java.util.ArrayList;
import java.util.List;

public class IRUtils {
	public static List<IRStmt> flattenSeq(List<IRStmt> stmts) {
		List<IRStmt> newSeq = new ArrayList<>();
		for(IRStmt stmt : stmts) {
			if(stmt instanceof IRSeq) newSeq.addAll(flattenSeq(((IRSeq)stmt).stmts()));
			else newSeq.add(stmt);		
		}
		return newSeq;
	}
}