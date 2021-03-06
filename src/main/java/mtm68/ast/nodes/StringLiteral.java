package mtm68.ast.nodes;

public class StringLiteral extends Literal<String>{

	public StringLiteral(String value) {
		super(value);
	}
	
	@Override
	public String toString() {
		return "\"" + value + "\"";
	}
}
