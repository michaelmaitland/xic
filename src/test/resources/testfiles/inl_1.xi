use io

main(args: int[][]) {
  i:int = 0
  y:int = 0

  while(i < 500000000){
  	y = f(2)
  	i = inc(i);
  }
  
  print(unparseInt(y));
}

f(x : int) : int{
	return x;
}

inc(x:int) : int{
	return x + 1
}
