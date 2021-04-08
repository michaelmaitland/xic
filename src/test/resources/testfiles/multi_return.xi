use io

main(args : int[][]){
	x:int, y:int = returnIntTuple();
	println("(" + unparseInt(x) + ", " + unparseInt(y) + ")")
	 
	w:int, z:int, _ = returnIntThruple();
	println("(" + unparseInt(w) + ", " + unparseInt(z) + ")")
	
	m:int, b:bool = returnMixedTuple();
	
	if(b){
		println(unparseInt(m));
	}
	
	s1:int[], s2:int[] = returnStringTuple();
	println(s1 + " " + s2);
}

returnIntTuple() : int, int {
	return 2, 3;
}

returnIntThruple() : int, int, int {
	return 1, 2, 3;
}

returnMixedTuple() : int, bool {
	return 1, true;
}

returnStringTuple() : int[], int[]{
	return "first", "second";
}

retSing(i : int[]) : int[]{
	return i;
}