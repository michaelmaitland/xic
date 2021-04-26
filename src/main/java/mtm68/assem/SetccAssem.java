package mtm68.assem;

public class SetccAssem extends Assem {
	
	private CC cc;
	
	public SetccAssem(CC cc) {
		this.cc = cc;
	}
	
	@Override
	public String toString() {
		return "set" + cc + " al";
	}
	
	public static enum CC {
		G,
		GE,
		L,
		LE,
		E,
		B,
		NE;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
