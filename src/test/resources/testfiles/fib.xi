use io

main(args:int[][]) {
	print(unparseInt(fib(9)))
}

fib(n:int) : int
{
   if (n <= 1) { return n }
   return fib(n-1) + fib(n-2);
}