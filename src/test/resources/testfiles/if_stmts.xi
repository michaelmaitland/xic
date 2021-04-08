use io

main(args:int[][]) {
   	booly(true, true)
   	booly(true, false)
   	booly(false, true)
   	booly(false, false)
}

booly(b1 : bool, b2 : bool) {
    if (b1) { print("1") }
    else if (b2) { print("2") }
    else { print("3") }
}