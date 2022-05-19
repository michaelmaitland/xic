use io
use conv

class A {
  f() : int {
    return 42
  }

  init() : A {
    return this
  }
}

main(args : int[][]) {
  a : A = new A.init()
  x : int = a.f()
  print(unparseInt(x))
}
