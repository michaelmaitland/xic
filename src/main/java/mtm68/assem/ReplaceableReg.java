package mtm68.assem;

import java.util.List;
import java.util.function.Consumer;

import mtm68.assem.operand.AbstractReg;
import mtm68.assem.operand.Dest;
import mtm68.assem.operand.Mem;
import mtm68.assem.operand.RealReg;
import mtm68.assem.operand.Reg;
import mtm68.assem.operand.Src;
import mtm68.util.ArrayUtils;

/**
 * Represents an object that can have its registers replaced with real registers
 * during register allocation. 
 * 
 * @author Scott
 */
public class ReplaceableReg {
	
	private String name;
	private Reg initialReg;
	private RegType regType;
	private Consumer<RealReg> replace;
	private boolean isAbstract;
	
	public ReplaceableReg(String name, Reg initialReg, RegType regType, Consumer<RealReg> replace, boolean isAbstract) {
		super();
		this.name = name;
		this.initialReg = initialReg;
		this.regType = regType;
		this.replace = replace;
		this.isAbstract = isAbstract;
	}
	
	public static List<ReplaceableReg> fromDest(Dest dest, Consumer<RealReg> replace){
		if(dest instanceof Mem) {
			return fromMem((Mem)dest);
		} else if (dest instanceof Reg){
			return ArrayUtils.singleton(fromReg((Reg)dest, RegType.WRITE, replace));
		}

		return ArrayUtils.empty();
	}
	
	public static List<ReplaceableReg> fromDestRead(Dest dest, Consumer<RealReg> replace){
		if(dest instanceof Mem) {
			return fromMem((Mem)dest);
		} else if (dest instanceof Reg){
			return ArrayUtils.singleton(fromReg((Reg)dest, RegType.READ, replace));
		}

		return ArrayUtils.empty();
	}

	public static List<ReplaceableReg> fromSrc(Src src, Consumer<RealReg> replace){
		if(src instanceof Mem) {
			return fromMem((Mem)src);
		} else if (src instanceof Reg){
			return ArrayUtils.singleton(fromReg((Reg)src, RegType.READ, replace));
		}

		return ArrayUtils.empty();
	}
	
	public static ReplaceableReg fromRealReg(RealReg reg, RegType regType) {
		return fromReg(reg, regType, r -> {});
	}
	
	private static List<ReplaceableReg> fromMem(Mem mem){
		return mem.getReplaceableRegs();
	}

	private static ReplaceableReg fromReg(Reg reg, RegType regType, Consumer<RealReg> replace){
		return new ReplaceableReg(reg.getId(), reg, regType, replace, reg instanceof AbstractReg);
	}
	

	public String getName() {
		return name;
	}

	public Reg getInitialReg() {
		return initialReg;
	}

	public RegType getRegType() {
		return regType;
	}
	
	public void replace(RealReg realReg) {
		replace.accept(realReg);
	}
	
	public boolean isAbstract() {
		return isAbstract;
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
