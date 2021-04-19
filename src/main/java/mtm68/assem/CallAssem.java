package mtm68.assem;

public class CallAssem extends OneOpAssem {

	private String name;

	public CallAssem(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "call " + name;
	}
	
}
