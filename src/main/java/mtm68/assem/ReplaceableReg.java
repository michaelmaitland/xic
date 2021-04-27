package mtm68.assem;

import java.util.List;
import java.util.function.Consumer;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

public class ReplaceableReg {
	
	private String name;
	private RegType regType;
	private Consumer<RealReg> replace;
	
	public ReplaceableReg(String name, RegType regType, Consumer<RealReg> replace) {
		super();
		this.name = name;
		this.regType = regType;
		this.replace = replace;
	}
	
	public static List<ReplaceableReg> fromDest(Dest dest, Consumer<RealReg> replace){
		if(dest instanceof Mem) {
			return fromMem((Mem)dest);
		} else if (dest instanceof AbstractReg){
			return ArrayUtils.singleton(fromAbstractReg((AbstractReg)dest, RegType.WRITE, replace));
		}

		return ArrayUtils.empty();
	}

	public static List<ReplaceableReg> fromSrc(Src src, Consumer<RealReg> replace){
		if(src instanceof Mem) {
			return fromMem((Mem)src);
		} else if (src instanceof AbstractReg){
			return ArrayUtils.singleton(fromAbstractReg((AbstractReg)src, RegType.READ, replace));
		}

		return ArrayUtils.empty();
	}
	
	private static List<ReplaceableReg> fromMem(Mem mem){
		return mem.getReplaceableRegs();
	}

	private static ReplaceableReg fromAbstractReg(AbstractReg reg, RegType regType, Consumer<RealReg> replace){
		return new ReplaceableReg(reg.getId(), regType, replace);
	}

	public String getName() {
		return name;
	}

	public RegType getRegType() {
		return regType;
	}
	
	public void replace(RealReg realReg) {
		replace.accept(realReg);
	}

	@Override
	public String toString() {
		return "ReplaceableReg [name=" + name + ", regType=" + regType + ", replace=" + replace + "]";
	}

	public enum RegType {
		READ,
		WRITE;
	}

}
