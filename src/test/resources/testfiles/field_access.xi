use io
use conv

class A {
  x : int;

  init() : A {
    x = 33
    return this
  }
}

main(args : int[][]) {
  a : A = new A.init()
  x : int = a.x
  print(unparseInt(x))
}
