package mtm68.assem;

public class Setcc extends Assem {
	
	private CC cc;
	
	public Setcc(CC cc) {
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
		NE;
		
		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

}
