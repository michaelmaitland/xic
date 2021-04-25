package mtm68.assem;

public class LabelAssem extends Assem {
	private String name;
	
	public LabelAssem(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name + ":";
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}
