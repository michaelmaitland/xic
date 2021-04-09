doSomething (b: bool): int, int {
	if b == true {
		return 0, 1 
	} else {
		return 1, 0
	}
}

f () : int {
	return 10
}

main (args: int[]) {
	y:int[] = "hello"
    x:int = 5 + f()
    z:int = x + y[0]
    
    a:int, b:int = doSomething(false)
}