use io

main(args: int[][]) {
  i:int = 0

  while(i < 500000000){
	f(2);
  	i = inc(i);
  }
}

f(x : int){
	x = x + 2
}

inc(x:int) : int{
	return x + 1
}
