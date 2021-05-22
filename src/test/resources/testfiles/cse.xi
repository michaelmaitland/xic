use io
main(args: int[][]) {
	a : int = 1
  b : int = 2
  c : int = 3
  d : int = 4
  e : int = 5
  f : int = 6
  g : int = 7
  h : int = e+b-f*a/c+d%c-a/a/d+g+d-a-g-f*e*d/b+d-c/f+a*a/a+c-f/b+a/a*f

  i : int = 0;
  while(i < 1000000){
	j : int = e+b-f*a/c+d%c-a/a/d+g+d-a-g-f*e*d/b+d-c/f+a*a/a+c-f/b+a/a*f
    i = i + 1
  }
  print("done")
}
