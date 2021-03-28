package mtm68.ast.nodes;

public abstract class Literal<T> extends Expr {
	
	protected T value;
	
	public Literal(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
