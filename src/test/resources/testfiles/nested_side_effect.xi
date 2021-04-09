use io

main(args:int[][]) {
   	print(unparseInt(nestedSideEffect(true, true, 0)))
   	print(unparseInt(nestedSideEffect(true, false, 0)))
   	print(unparseInt(nestedSideEffect(false, true, 0)))
   	print(unparseInt(nestedSideEffect(false, false, 0)))
}

nestedSideEffect(b1 : bool, b2 : bool, i : int) : int {
   if(b1) {
     if(b2) {
       i = 1
     } else {
       i = 2
     }
   } else {
     if(b2) {
       i = 3
    } else {
       i = 4
    }
   }
   return i
}