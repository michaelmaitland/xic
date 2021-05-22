use io

main(args: int[][]) {
  i:int = 0

  while(i < 500000000){
  	a:int, b:int, _ = f(2)
  	i = inc(i);
  }
}

f(x : int) : int, int, bool{
	return x, x + 3, true;
}

inc(x:int) : int{
	return x + 1
}
