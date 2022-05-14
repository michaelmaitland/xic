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
  // a = A.init()
  // x = a.f()
  // print(unparseInt(x))
}
