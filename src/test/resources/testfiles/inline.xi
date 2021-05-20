use io

main(args : int[][]){
	x : int = id(3);
	simpleRec(x); //Should terminate
	
	
}

id(x : int) : int{
	return idTwo(x);
}

idTwo(x : int) : int{
	return x;
}

simpleRec(x : int){
	if(x != 0) {
		simpleRec(x-1);
	}
}