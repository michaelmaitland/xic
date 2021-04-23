package mtm68.assem.pattern;

public class SmallConstantPattern extends ConstantPattern {

	public SmallConstantPattern(String name) {
		super(name);
	}
	
	@Override
	protected boolean valueMatches(long value) {
		return value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE;
	}
}