use io

main(args : int[][]){
	c1 : int, c2 : int, c3 : int, c4 : int = incrementFourCounters(4);
	
	println("c1: " + unparseInt(c1)); // 4
	println("c2: " + unparseInt(c2)); // 8
	println("c3: " + unparseInt(c3)); // 12
	println("c4: " + unparseInt(c4)); // 16
	
}

incrementFourCounters(idx : int) : int, int, int, int{
	if(idx == 0){
		return 0, 0, 0, 0;
	}
	else{
		a : int, b : int, c : int, d : int = incrementFourCounters(idx - 1);
		return a + 1, b + 2, c + 3, d + 4;
	}
}