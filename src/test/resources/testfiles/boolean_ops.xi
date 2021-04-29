use io
use conv

main(args : int[][]){
	t:bool = true;
	f:bool = false;
	
	x:int = 5;
	y:int = 3;
	z:int = 5;
	
	print(b2s(t == t)); // 1
	print(b2s(t == f)); // 0
	print(b2s(f == f)); // 1
	print(b2s(t == f)); // 0
	
	print(b2s(t != t)); // 0
	print(b2s(t != f)); // 1
	print(b2s(f != f)); // 0
	print(b2s(t != f)); // 1
	
	print(b2s(x == y)); // 0
	print(b2s(x == x)); // 1
	print(b2s(x == z)); // 1

	print(b2s(x != y)); // 1
	print(b2s(x != x)); // 0
	print(b2s(x != z)); // 0
	
	print(b2s(x <= y)); // 0
	print(b2s(x <= x)); // 1
	print(b2s(x <= z)); // 1
	print(b2s(y <= x)); // 1
	
	print(b2s(x < y)); // 0
	print(b2s(x < x)); // 0
	print(b2s(x < z)); // 0
	print(b2s(y < x)); // 1
	
	print(b2s(x > y)); // 1
	print(b2s(x > x)); // 0
	print(b2s(x > z)); // 0
	print(b2s(y > x)); // 0
	
	print(b2s(x >= y)); // 1
	print(b2s(x >= x)); // 1
	print(b2s(x >= z)); // 1
	print(b2s(y >= x)); // 0
}

b2s(b: bool): int[] {
    if (b) { return unparseInt(1) } else { return unparseInt(0) }
}