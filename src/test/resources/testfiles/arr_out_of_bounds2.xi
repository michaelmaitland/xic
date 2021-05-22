use io

main(args : int[][]){
	x:int = outOfBounds()
}

outOfBounds() : int {
	x : int[2];
	x[5] = 1;
	return 2;
}