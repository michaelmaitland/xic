use io

main(args: int[][]) {
  i:int = 0
  a:int = 0
  b:int = 0
  c:int = 0
  d:int = 0
  e:int = 0
  f:int = 0
  
	

  while(i < 40000000){
  	a = i / 2
  	b = (i / 2) * 3
  	c = (i / 2) * 3 + 5
  	d = ((i / 2) * 3 + 5) * 3
  	e = (((i / 2) * 3 + 5) * 3) / 3
  	f = (((i / 2) * 3 + 5) * 3) / 3 + 7
  	f = i / 2
  	b = (i / 2) * 3 + 5

    i = i + 1
  }
  
  t:int = a + b + c + d + e + f
}
