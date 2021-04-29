use io
use conv

main(args : int[][]){
	t:bool = true;
	f:bool = false;
	
	x:int = 5;
	y:int = 3;
	z:int = 20;
	
	arr:int[] = {0, 1, 2}
	
	print(b2s(t & t)); // 1
	print(b2s(t | f)); // 1
	print(b2s(f | f)); // 0
	print(b2s(t & f)); // 0
	
	print(b2s(x == x)); // 1
	print(b2s(x == y)); // 0
	print(b2s(x != x)); // 0
	print(b2s(x != y)); // 1
	
	print(b2s(x < x)); // 0
	print(b2s(x <= x)); // 1
	print(b2s(y < x)); // 1
	print(b2s(x >= x)); // 1
	print(b2s(z < x)); // 0
	print(b2s(z > x)); // 1
	
	print(unparseInt(x + y)); // 8
	print(unparseInt(x - y)); // 2
	print(unparseInt(x + y - z)); // -12
	print(unparseInt(-x - y)); // -8
	
	print(unparseInt(z / x)); // 4
	print(unparseInt(z / y)); // 6
	
	print(unparseInt(z % x)); // 0
	print(unparseInt(z % y)); // 2
	print(unparseInt(z % 6)); // 2
	
	print(unparseInt(x * y * 2)); // 30
	print(unparseInt(z * y / x / 4 + arr[2])); // 5
	
	print(unparseInt(arr[z / 10])); // 2
	print(unparseInt(arr[arr[1]])); // 1
	
	print(unparseInt(z % -6)); // 2
	print(unparseInt(z / -5)); // -4
	print(unparseInt(-20 / z)); // -1
	
}

b2s(b: bool): int[] {
    if (b) { return unparseInt(1) } else { return unparseInt(0) }
}