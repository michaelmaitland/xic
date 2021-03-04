package mtm68.ast.nodes;

public class TempMain {
	
	public static void main(String[] args) {
		// 2 + 3 * 7
		
		Node prog = new BinExpr(
				Binop.ADD, 
				new IntLiteral(2), 
				new BinExpr(
						Binop.MULT, 
						new IntLiteral(3), 
						new IntLiteral(7)));
		
	}
}
