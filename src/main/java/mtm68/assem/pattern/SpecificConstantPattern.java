package mtm68.assem.pattern;

public class SpecificConstantPattern extends ConstantPattern {

	private long target;

	public SpecificConstantPattern(String name, long target) {
		super(name);
		this.target = target;
	}

	@Override
	protected boolean valueMatches(long value) {
		return target == value;
	}

}
