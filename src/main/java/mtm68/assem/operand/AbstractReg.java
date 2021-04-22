package mtm68.assem.operand;

public class AbstractReg extends Reg {
	
	public AbstractReg(String id) {
		this.id = id;
	}

	@Override
	public boolean isReg() {
		return true;
	}

	@Override
	public Reg getReg() {
		return this;
	}

	@Override
	protected AbstractReg getAbstractReg() {
		// TODO Auto-generated method stub
		return this;
	}
}
