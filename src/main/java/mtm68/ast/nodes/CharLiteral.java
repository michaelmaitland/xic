package mtm68.ast.nodes;

public class CharLiteral extends Literal<Character>{

	public CharLiteral(Character value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return "'" + value + "'";
	}
}
