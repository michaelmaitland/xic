use io

main(args:int[][]) {
	print(unparseInt(fib(9)))
}

fib(n:int) : int
{
	f:int[n+2];
	i:int = 2;
	
	f[0] = 0;
	f[1] = 1;
	
	while(i <= n) {
	  f[i] = f[i-1] + f[i-2];	
	  i = i + 1;
	}
	
	return f[n]
}

